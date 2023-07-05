import { EncounterAssociation, EncounterSummary, EncounterSummaryStatus, IEncounterSummary } from '@/data/ingest.data';
import { UploadDto } from '@/dtos/encounter.dto';
import { UserDetailsDto } from '@/dtos/user.dto';
import { Encounter } from '@/interfaces/uploads.interface';
import { EncounterModel, EncounterSummaryModel } from '@/models/encounters.model';
import { Types } from 'mongoose';
import { Service } from 'typedi';

@Service()
export class EncounterService {
  /// Encounters

  public async countEncounters(): Promise<number> {
    return await EncounterModel.countDocuments().exec();
  }

  public async createEncounter(id: Types.ObjectId, upload: UploadDto): Promise<Encounter> {
    return new Promise((resolve, reject) => {
      const objectId = new Types.ObjectId(id);
      const encounter: Encounter = { _id: objectId, data: upload };
      EncounterModel.create(encounter)
        .then(created => {
          this.setSummaryComplete(objectId).then(() => resolve(created), reject);
        })
        .catch(err => {
          this.setSummaryFailed(objectId, err.message).then(() => reject(err), reject);
        });
    });
  }

  public async encounterExists(id: Types.ObjectId): Promise<boolean> {
    const exists = await EncounterModel.exists({ _id: id }).exec();
    return exists !== null;
  }

  public async findEncounter(id: Types.ObjectId) {
    const encounter = await EncounterModel.findById(id).exec();
    if (!encounter) throw new Error('Encounter not found');
    return encounter;
  }

  public async updateEncounter(id: Types.ObjectId, update: any): Promise<Encounter> {
    return await EncounterModel.findByIdAndUpdate(id, update, { new: true, upsert: true }).exec();
  }

  /// Summaries

  public async findEncounterSummary(id: Types.ObjectId) {
    const summary = EncounterSummaryModel.findById(id).exec();
    if (!summary) throw new Error('Encounter summary not found');
    return summary;
  }

  public async createSummary(association: EncounterAssociation, uploader: UserDetailsDto, upload: UploadDto): Promise<IEncounterSummary> {
    const summary = new EncounterSummary(association, uploader, upload);
    summary.setParticipantUserId(uploader.user.id, upload.localPlayer);
    return await EncounterSummaryModel.create(summary);
  }

  public async removeSummary(id: Types.ObjectId): Promise<IEncounterSummary> {
    return await EncounterSummaryModel.findByIdAndDelete(id).exec();
  }

  public async existsSummary(id: Types.ObjectId): Promise<boolean> {
    const exists = await EncounterSummaryModel.exists({ _id: id }).exec();
    return exists !== null;
  }

  public async findSummaryByAssociation(association: EncounterAssociation) {
    return await EncounterSummaryModel.findOne({
      association: association,
    }).exec();
  }

  public async addUserToSummary(id: Types.ObjectId, userId: string, entityId: string) {
    const update = { $addToSet: { users: new Types.ObjectId(userId) }, $set: { 'participants.$.userId': userId } };
    await EncounterSummaryModel.updateOne({ _id: id, 'participants.id': entityId }, update).exec();
  }

  public async setSummaryStatus(id: Types.ObjectId, status: EncounterSummaryStatus): Promise<IEncounterSummary> {
    const update = { status: status };
    return await this.updateSummary(id, update);
  }

  public async setSummaryFailed(id: Types.ObjectId, error: string): Promise<IEncounterSummary> {
    const update = { status: EncounterSummaryStatus.FAILED, error: error };
    return await this.updateSummary(id, update);
  }

  public async setSummaryComplete(id: Types.ObjectId): Promise<IEncounterSummary> {
    const update = { status: EncounterSummaryStatus.SUCCESS };
    return await this.updateSummary(id, update);
  }

  private async updateSummary(id: Types.ObjectId, update: any): Promise<IEncounterSummary> {
    return await EncounterSummaryModel.findByIdAndUpdate(id, update, { new: true, upsert: true }).exec();
  }
}
