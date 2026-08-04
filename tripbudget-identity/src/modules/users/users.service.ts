import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { UserEntity } from './entities/user.entity';
import { RegisterDto } from '../auth/dtos/auto.dto';

@Injectable()
export class UsersService {
  constructor(
    @InjectRepository(UserEntity)
    private readonly userRepository: Repository<UserEntity>,
  ) {}

  async findByCondition(
    condition: Record<string, any>,
  ): Promise<UserEntity | null> {
    try {
      return this.userRepository.findOne({ where: condition });
    } catch (error) {
      throw new Error(
        `Failed to find user by ${JSON.stringify(condition)}: ${error.message}`,
      );
    }
  }

  async create(dto: RegisterDto): Promise<UserEntity> {
    const { email, password, name } = dto;
    try {
      const user = this.userRepository.create({
        email,
        password,
        name,
      });
      return this.userRepository.save(user);
    } catch (error) {
      throw new Error(`Failed to create user: ${error.message}`);
    }
  }
}
