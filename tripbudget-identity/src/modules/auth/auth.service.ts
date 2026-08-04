import {
  ConflictException,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import * as bcrypt from 'bcrypt';
import { UserStatusEnum } from 'src/config/enums/user.enum';
import { UsersService } from '../users/users.service';
import { LoginDto, RegisterDto, SessionData } from './dtos/auto.dto';
import { createHmac, randomBytes, randomUUID, timingSafeEqual } from 'crypto';
import { ConfigService } from '@nestjs/config';
import { RedisService } from 'src/common/redis/redis.service';
import { JwtService } from '@nestjs/jwt';

@Injectable()
export class AuthService {
  private readonly refreshTtlSeconds = 60 * 60 * 24 * 15; // 15 days
  constructor(
    private readonly usersService: UsersService,
    private readonly config: ConfigService,
    private readonly redis: RedisService,
    private readonly jwtService: JwtService,
  ) {}

  async register(dto: RegisterDto) {
    const email = dto.email.trim().toLowerCase();

    const existedUser = await this.usersService.findByCondition({ email });

    if (existedUser) {
      throw new ConflictException('Email already exists');
    }

    const passwordHash = await bcrypt.hash(dto.password, 12);

    const user = await this.usersService.create({
      email,
      name: dto.name.trim(),
      password: passwordHash,
    });

    return {
      id: user.id,
      email: user.email,
      name: user.name,
      avatarUrl: user.avatarUrl,
    };
  }

  async login(dto: LoginDto) {
    try {
      const email = dto.email.trim().toLowerCase();

      const user = await this.usersService.findByCondition({ email });

      if (!user) {
        throw new UnauthorizedException('Invalid credentials');
      }

      const passwordMatched = await bcrypt.compare(dto.password, user.password);

      if (!passwordMatched) {
        throw new UnauthorizedException('Invalid credentials');
      }

      if (user.status !== UserStatusEnum.ACTIVE) {
        throw new UnauthorizedException('Account is not active');
      }

      return this.createSession(user.id);
    } catch (error) {
      throw new UnauthorizedException('Invalid credentials');
    }
  }

  async logout(userId: string, sessionId: string) {
    try {
      await this.revokeSession(userId, sessionId);
    } catch (error) {
      throw new UnauthorizedException('Invalid session');
    }
  }

  async refresh(refreshToken: string) {
    const parts = refreshToken.split('.');

    // Check format {sessionId}.{randomSecret}
    if (parts.length !== 2) {
      throw new UnauthorizedException('Invalid refresh token');
    }

    const [sessionId, secret] = parts;

    if (!sessionId || !secret) {
      throw new UnauthorizedException('Invalid refresh token');
    }

    const sessionKey = this.getSessionKey(sessionId);

    const sessionRaw = await this.redis.redisClient.get(sessionKey);

    // Redis key not found: session expired or revoked.
    if (!sessionRaw) {
      throw new UnauthorizedException('Session expired or revoked');
    }

    const session = JSON.parse(sessionRaw) as SessionData;

    // hash random secret from refresh token and compare with hash stored in session.
    const incomingHash = this.hashRefreshSecret(secret);

    if (!this.safeHashEquals(incomingHash, session.refreshTokenHash)) {
      // revoke if use old refresh token or refresh token is tampered.
      await this.revokeSession(session.userId, sessionId);

      throw new UnauthorizedException('Invalid refresh token');
    }

    return this.rotateSession(sessionId, session);
  }

  private async rotateSession(sessionId: string, session: SessionData) {
    const remainingSeconds = Math.floor(
      (new Date(session.expiresAt).getTime() - Date.now()) / 1000,
    );

    if (remainingSeconds <= 0) {
      await this.revokeSession(session.userId, sessionId);

      throw new UnauthorizedException('Session expired');
    }

    const newRefreshSecret = randomBytes(48).toString('base64url');

    session.refreshTokenHash = this.hashRefreshSecret(newRefreshSecret);

    await this.saveSession(sessionId, session, remainingSeconds);

    return {
      accessToken: await this.jwtService.signAsync({
        sub: session.userId,
        sid: sessionId,
        jti: randomUUID(),
        type: 'access',
      }),
      refreshToken: `${sessionId}.${newRefreshSecret}`,
    };
  }

  private safeHashEquals(left: string, right: string) {
    const leftBuffer = Buffer.from(left);
    const rightBuffer = Buffer.from(right);

    return (
      leftBuffer.length === rightBuffer.length &&
      timingSafeEqual(leftBuffer, rightBuffer)
    );
  }

  private async revokeSession(userId: string, sessionId: string) {
    try {
      await this.redis.redisClient
        .multi()
        .del(this.getSessionKey(sessionId))
        .srem(this.getUserSessionsKey(userId), sessionId)
        .exec();
    } catch (error) {
      throw new UnauthorizedException('Invalid session');
    }
  }
  private async createSession(userId: string | number) {
    const sessionId = randomUUID();

    const refreshSecret = randomBytes(48).toString('base64url');

    const expiresAt = new Date(
      Date.now() + this.refreshTtlSeconds * 1000,
    ).toISOString();

    const session: SessionData = {
      userId: String(userId),
      refreshTokenHash: this.hashRefreshSecret(refreshSecret),
      createdAt: new Date().toISOString(),
      expiresAt,
    };

    await this.saveSession(sessionId, session, this.refreshTtlSeconds);
    const accessToken = await this.jwtService.signAsync({
      sub: userId,
      sid: sessionId,
      jti: randomUUID(),
      type: 'access',
    });

    return {
      accessToken,
      // Only controller allow to give refreshToken to HttpOnly cookie.
      refreshToken: `${sessionId}.${refreshSecret}`,
    };
  }

  private async saveSession(
    sessionId: string,
    session: SessionData,
    ttlSeconds: number,
  ) {
    const userSessionsKey = this.getUserSessionsKey(session.userId);

    await this.redis.redisClient
      .multi()
      .set(
        this.getSessionKey(sessionId),
        JSON.stringify(session),
        'EX',
        ttlSeconds,
      )
      .sadd(userSessionsKey, sessionId)
      // .expire(userSessionsKey, ttlSeconds)
      .exec();
  }

  private getSessionKey(sessionId: string) {
    return `auth:session:${sessionId}`;
  }

  private getUserSessionsKey(userId: string) {
    return `auth:user-sessions:${userId}`;
  }

  private hashRefreshSecret(secret: string) {
    return createHmac(
      'sha256',
      this.config.getOrThrow<string>('REFRESH_TOKEN_PEPPER'),
    )
      .update(secret)
      .digest('hex');
  }
}
