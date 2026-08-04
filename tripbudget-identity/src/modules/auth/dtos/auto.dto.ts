import { IsEmail, IsNotEmpty, MinLength } from 'class-validator';

export class RegisterDto {
  @IsEmail()
  @IsNotEmpty()
  email: string;

  @MinLength(8)
  password: string;

  @IsNotEmpty()
  name: string;
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
