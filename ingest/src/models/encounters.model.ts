import { model, Schema, Document, Types } from 'mongoose';
import { Encounter } from '@interfaces/uploads.interface';
import { IEncounterSummary } from '@/data/ingest.data';

const EncounterSchema: Schema = new Schema({
  data: {
    type: Object,
    required: true,
  },
});

export const EncounterModel = model<Encounter & Document>('encounters', EncounterSchema);

const EncounterSummarySchema: Schema = new Schema({
  visibility: {
    type: String,
    required: true,
  },
  association: {
    type: Object,
    required: true,
  },
  owner: {
    type: Types.ObjectId,
    required: true,
  },
  users: {
    type: Array,
    required: true,
  },
  participants: {
    type: Array,
    required: true,
  },
  duration: {
    type: Number,
    required: false,
  },
  created: {
    type: Number,
    required: true,
  },
  status: {
    type: String,
    required: true,
  },
  error: {
    type: String,
    required: false,
  },
});

export const EncounterSummaryModel = model<IEncounterSummary & Document>('encounterSummaries', EncounterSummarySchema);
