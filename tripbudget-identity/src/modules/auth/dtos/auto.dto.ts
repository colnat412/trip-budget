import { IsEmail, IsNotEmpty, MinLength } from 'class-validator';
import { UserStatusEnum } from 'src/config/enums/user.enum';

export class RegisterDto {
  @IsEmail()
  @IsNotEmpty()
  email: string;

  @MinLength(8)
  password: string;

  @IsNotEmpty()
  name: string;

  status?: UserStatusEnum;
}

export class LoginDto {
  @IsEmail()
  @IsNotEmpty()
  email: string;

  @MinLength(8)
  password: string;
}

export class SessionData {
  userId: string;
  refreshTokenHash: string;
  expiresAt: string;
  createdAt: string;
}

export class VerifyOtpDto {
  @IsEmail()
  @IsNotEmpty()
  email: string;

  @IsNotEmpty()
  @MinLength(6)
  otp: string;
}

export class ResendOtpDto {
  @IsEmail()
  @IsNotEmpty()
  email: string;
}

export class GoogleLoginDto {
  @IsNotEmpty()
  credential: string; // ID token from Google
}
