import { Global, Module } from '@nestjs/common';
import { HashidsService } from './hashids.service';

@Global()
@Module({
  providers: [HashidsService],
  exports: [HashidsService],
})
export class HashidsModule {}
