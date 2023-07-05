import { UploadDto } from '@/dtos/encounter.dto';
import mongoose from 'mongoose';

export interface Encounter {
  _id: mongoose.Types.ObjectId;
  data: UploadDto;
}
