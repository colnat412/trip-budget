import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { RedisModule } from './common/redis/redis.module';
import { AppModules } from './config/modules';
import { PostgresModule } from './databases/postgres/postgres.module';
import { PassportModule } from '@nestjs/passport';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: ['.env'],
    }),

    PostgresModule,
    RedisModule,
    ...AppModules,
  ],
})
export class AppModule {}
