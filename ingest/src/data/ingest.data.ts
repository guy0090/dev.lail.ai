import { UploadDto, Entity } from '@/dtos/encounter.dto';
import { UserDetailsDto } from '@/dtos/user.dto';
import { getZone, ZoneType } from '@/game/zones';
import { PrivacySetting, User } from '@/interfaces/users.interface';
import { logger } from '@/utils/logger';
import { Types } from 'mongoose';

export enum EncounterSummaryStatus {
  FAILED = 'FAILED',
  PROCESSING = 'PROCESSING',
  SUCCESS = 'SUCCESS',
}

export class EncounterAssociation {
  players: string;
  zone: number;

  constructor(players: string, zone: number) {
    this.players = players;
    this.zone = zone;
  }

  toString() {
    return `${this.players}|${this.zone}`;
  }

  static fromString(association: string) {
    const [players, zone] = association.split('|');
    return new EncounterAssociation(players, Number(zone));
  }
}

export class Participant {
  id: string;
  name: string;
  classId: number;
  gearScore: number;
  damage: number;
  dps: number;
  partyId?: string;
  userId?: string;

  constructor(duration: number, entity: Entity) {
    const dps = entity.damageDealt / (duration / 1000);

    this.id = entity.id;
    this.name = entity.name;
    this.classId = entity.classId;
    this.gearScore = entity.gearScore;
    this.damage = entity.damageDealt;
    this.dps = dps;
    this.partyId = entity.partyId;
  }
}

export interface IEncounterSummary {
  _id: Types.ObjectId;
  visibility: PrivacySetting;
  association: EncounterAssociation;
  owner: Types.ObjectId;
  users: Types.ObjectId[];
  participants: Participant[];
  duration: number;
  created: number;
  status: EncounterSummaryStatus;
  error?: string;
}

export class EncounterSummary implements IEncounterSummary {
  _id: Types.ObjectId;
  visibility: PrivacySetting;
  association: EncounterAssociation;
  owner: Types.ObjectId;
  users: Types.ObjectId[];
  participants: Participant[];
  duration: number;
  created: number;
  status: EncounterSummaryStatus;
  error?: string;

  constructor(association: EncounterAssociation, uploader: UserDetailsDto, upload: UploadDto) {
    this.visibility = uploader.settings.uploadPrivacySetting;
    this.association = association;
    this.owner = new Types.ObjectId(uploader.user.id);
    this.users = [new Types.ObjectId(uploader.user.id)];
    this.duration = upload.lastCombatPacket - upload.fightStartedOn;
    this.createParticipants(this.duration, upload.entities);
    this.created = Date.now();
    this.status = EncounterSummaryStatus.PROCESSING;
  }

  protected createParticipants(duration: number, entities: Entity[]) {
    if (this.participants) return;

    this.participants = [];
    entities.forEach(entity => {
      if (!entity.isPlayer) return;
      this.participants.push(new Participant(duration, entity));
    });
  }

  setParticipantUserId(userId: string, participantId: string) {
    const participant = this.participants.find(p => p.id === participantId);
    if (participant) participant.userId = userId;
  }
}

export class Pending {
  static TIMEOUT_DURATION = 15000;

  id: Types.ObjectId;
  association: EncounterAssociation;
  uploaders: User[];
  uploads: UploadDto[];
  timestamp: number = Date.now();

  constructor(id: Types.ObjectId, association: EncounterAssociation, uploader: User, upload: UploadDto) {
    this.id = id;
    this.association = association;
    this.uploaders = [uploader];

    upload.assignUserId(uploader.id, upload.localPlayer);
    this.uploads = [upload];
  }

  addUpload(uploader: User, upload: UploadDto): boolean {
    const uploaderExists = this.uploaders.find(u => `${u.id}` === `${uploader.id}`);

    if (!uploaderExists) {
      logger.debug(
        `Pending:: Adding ${uploader.id}(${uploader.discordUsername}#${uploader.discriminator}) to existing pending '${this.association}'`,
      );
      if (++this.uploads.length > 8) throw new Error('Too many uploads');
      if (++this.uploaders.length > 8) throw new Error('Too many uploaders');

      this.uploaders.push(uploader);
      this.uploads[0].assignUserId(uploader.id, upload.localPlayer);
      const zone = getZone(this.association.zone);
      if (zone.type !== ZoneType.GUARDIAN) this.uploads.push(upload); // Guardians will never have missing information so we don't need to support combining
      return true;
    } else {
      logger.debug(`Duplicate upload from ${uploader.id}(${uploader.discordUsername}#${uploader.discriminator}) `);
    }
    return false;
  }

  combine() {
    // TODO attempt to combine uploads into a single upload (if theres multiple parties)
  }
}
