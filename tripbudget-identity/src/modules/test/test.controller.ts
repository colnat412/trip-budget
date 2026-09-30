import { Controller, Get } from '@nestjs/common';
import type { Response } from 'express';
import { Public } from '../auth/decorators/public.decorator';

@Controller('test')
export class TestController {
  constructor() {}

  @Public()
  @Get()
  get() {
    return 'Connect Success';
  }
}
