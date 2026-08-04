import { Injectable, UnauthorizedException } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { RedisService } from 'src/common/redis/redis.service';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor(
    config: ConfigService,
    private readonly redis: RedisService,
  ) {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      secretOrKey: config.getOrThrow<string>('JWT_ACCESS_SECRET'),
    });
  }

  async validate(payload: any) {
    try {
      if (payload.type !== 'access') {
        throw new UnauthorizedException();
      }

      const sessionRaw = await this.redis.redisClient.get(
        `auth:session:${payload.sid}`,
      );

      if (!sessionRaw) {
        throw new UnauthorizedException("Session doesn't exist");
      }

      const session = JSON.parse(sessionRaw);
      if (session.userId !== payload.sub) {
        throw new UnauthorizedException("Session doesn't belong to user");
      }
      return payload;
    } catch (error) {
      throw new UnauthorizedException();
    }
  }
}
