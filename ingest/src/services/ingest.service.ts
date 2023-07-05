import { Entity, UploadDto } from '@/dtos/encounter.dto';
import { logger } from '@/utils/logger';
import { Service, Container } from 'typedi';
import { EncounterService } from './encounters.service';
import { Zone } from '@/game/zones';
import { EncounterAssociation, Pending } from '@/data/ingest.data';
import { WebSocketService } from './websocket.service';
import { PublishPendingEncounterCommand } from '@/websocket/commands.websocket';
import { UserDetailsDto } from '@/dtos/user.dto';
import { Types } from 'mongoose';

@Service()
export class IngestService {
  protected static timeouts = new Map<string, NodeJS.Timeout>();
  protected static pendingMap = new Map<string, Pending>();
  protected static uploaders = new Map<string, number>();

  private encounters = Container.get(EncounterService);
  private ws = Container.get(WebSocketService);

  setTimeout(id: string, timeout: NodeJS.Timeout) {
    IngestService.timeouts.set(id, timeout);
  }

  clearTimeout(id: string) {
    const timeout = IngestService.timeouts.get(id);
    if (timeout) {
      clearTimeout(timeout);
    }
  }

  getPending(id: string) {
    return IngestService.pendingMap.get(id);
  }

  setPending(id: string, pending: Pending) {
    IngestService.pendingMap.set(id, pending);
  }

  deletePending(id: string) {
    IngestService.pendingMap.delete(id);
  }

  addPending(uploader: UserDetailsDto, upload: UploadDto, association: EncounterAssociation) {
    return new Promise<Types.ObjectId>((resolve, reject) => {
      const pending = this.getPending(association.toString());
      if (pending) {
        this.addToExistingPending(pending, uploader, upload).then(resolve, reject);
      } else {
        this.createNewPending(uploader, upload, association).then(resolve, reject);
      }
    });
  }

  addToExistingPending(entry: Pending, uploader: UserDetailsDto, upload: UploadDto) {
    return new Promise<Types.ObjectId>((resolve, reject) => {
      logger.info(`Adding upload from ${uploader.user.id} to pending ${entry.association}`);
      IngestService.addUploader(uploader.user.id);
      const added = entry.addUpload(uploader.user, upload);
      if (added) {
        this.encounters
          .addUserToSummary(entry.id, uploader.user.id, upload.localPlayer)
          .then(() => resolve(entry.id))
          .catch(err => {
            logger.error('Failed to add user to pending encounter', err.message);
            IngestService.removeUploader(uploader.user.id);
            reject(err);
          });
      } else {
        logger.info(`Upload from ${uploader.user.id} already exists in pending ${entry.association}`);
        IngestService.removeUploader(uploader.user.id);
        resolve(entry.id);
      }
    });
  }

  createNewPending(uploader: UserDetailsDto, upload: UploadDto, association: EncounterAssociation) {
    return new Promise<Types.ObjectId>((resolve, reject) => {
      logger.info(`Creating pending ${association} from ${uploader.user.id}`);
      IngestService.addUploader(uploader.user.id);

      this.encounters
        .createSummary(association, uploader, upload)
        .then(summary => {
          const pending = new Pending(summary._id, association, uploader.user, upload);
          this.setPending(association.toString(), pending);
          this.setTimeout(
            association.toString(),
            setTimeout(() => this.removePending(association.toString()), Pending.TIMEOUT_DURATION),
          );

          const publishCmd = new PublishPendingEncounterCommand(summary);
          this.ws.sendCommand(publishCmd).catch(err => {
            logger.error('Failed to publish pending encounter', err.message);
          });
          resolve(summary._id);
        })
        .catch(err => {
          logger.error('Failed to create pending encounter', err.message);
          IngestService.removeUploader(uploader.user.id);
          reject(err);
        });
    });
  }

  removePending(id: string) {
    const entry = this.getPending(id);
    if (!entry) return;

    this.encounters
      .createEncounter(entry.id, entry.uploads[0])
      .then(() => logger.debug(`Created encounter from pending '${id}'`))
      .catch(err => logger.error(`Failed to create encounter from pending '${id}'`, err.message));

    this.deletePending(id);
    this.clearTimeout(id);
    entry.uploaders.forEach(u => IngestService.removeUploader(u.id));

    logger.debug(`Pending upload '${id}' finished processing after ${Date.now() - entry.timestamp}ms`);
  }

  static getUploaders = () => IngestService.uploaders;
  static isUploading = (id: string) => IngestService.uploaders.has(id);

  static addUploader(id: string) {
    this.uploaders.set(id, Date.now());
  }

  static removeUploader(id: string) {
    if (!this.isUploading(id) || this.isUserInPending(id)) return false;
    return this.uploaders.delete(id);
  }

  static isUserInPending = (id: string) => {
    for (const pending of IngestService.pendingMap.values()) {
      if (pending.uploaders.some(u => u.id === id)) return true;
    }
    return false;
  };

  static getEncounterAssociation = (zone: Zone, entities: Entity[]) => {
    const players = [];

    for (const entity of entities) {
      if (entity.isPlayer) players.push(entity.id);
    }

    // Sort the array so that the association is always the same
    players.sort();

    return new EncounterAssociation(players.join(''), zone.id);
  };
}
