import { EncounterSummaryDto, EncounterDto, EncounterEntryDto } from "@/api/common/dto/ResponseDtos";
import { Buffer } from "buffer";

export class EncounterStoreEntry {
  summary: EncounterSummaryDto;
  encounter: EncounterDto;
  related: EncounterSummaryDto[];
  lastUpdated: number;

  constructor(summary: EncounterSummaryDto, encounter: EncounterDto, related?: EncounterSummaryDto[], lastUpdated?: number) {
    this.encounter = encounter;
    this.summary = summary;
    this.related = related ?? [];
    this.lastUpdated = lastUpdated || Date.now();
  }

  static fromApiDto = (data: EncounterEntryDto) => new EncounterStoreEntry(data.summary, data.encounter, data.related);


  static fromEntry(data: Uint8Array): EncounterStoreEntry {
    const entry = JSON.parse(Buffer.from(data).toString('utf-8'));
    const summary = new EncounterSummaryDto(entry.summary);
    const encounter = new EncounterDto(entry.encounter);
    const related = entry.related.map((r: any) => new EncounterSummaryDto(r));
    const lastUpdated = entry.lastUpdated;
    return new EncounterStoreEntry(summary, encounter, related, lastUpdated);
  }

  toBuffer(): Buffer {
    return Buffer.from(JSON.stringify(this), 'utf-8');
  }
}