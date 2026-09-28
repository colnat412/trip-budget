import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import Hashids from 'hashids';

@Injectable()
export class HashidsService {
  private readonly hashids: Hashids;

  constructor(private readonly configService: ConfigService) {
    const salt =
      this.configService.get<string>('HASHIDS_SALT') ||
      '6d8a1d503a90486cd6cef4fb216faabf';
    const minLength = Number(
      this.configService.get<string | number>('HASHIDS_MIN_LENGTH') || 8,
    );
    this.hashids = new Hashids(salt, minLength);
  }

  encode(id: number | string | bigint): string {
    if (id == null) return '';
    const num = typeof id === 'number' ? id : Number(id);
    if (isNaN(num)) return '';
    return this.hashids.encode(num);
  }

  decode(hashId: string | number): number | null {
    if (hashId == null) return null;
    const str = String(hashId).trim();
    if (!str) return null;

    const decoded = this.hashids.decode(str);
    if (decoded && decoded.length > 0) {
      return Number(decoded[0]);
    }

    if (/^\d+$/.test(str)) {
      return Number(str);
    }

    return null;
  }
}
