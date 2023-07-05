import type { Request } from 'express';
import type { UploadDto } from '@/dtos/encounter.dto';
import type { Zone } from '@/game/zones';
import { UserDetailsDto } from '@/dtos/user.dto';

export interface TokenData {
  exp: number;
  iat: number;
  iss: string;
  sub: string;
  payload: {
    id: string;
    hash: string;
  };
}

export interface RequestWithData extends Request {
  uploader: UserDetailsDto;
  upload: UploadDto;
  zone: Zone;
}
