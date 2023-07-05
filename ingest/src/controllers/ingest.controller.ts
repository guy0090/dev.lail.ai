import { Pending } from '@/data/ingest.data';
import { HttpException, HttpExceptionCode } from '@/exceptions/httpException';
import { RequestWithData } from '@/interfaces/auth.interface';
import { EncounterService } from '@/services/encounters.service';
import { IngestService } from '@/services/ingest.service';
import { logger } from '@/utils/logger';
import { NextFunction, Response } from 'express';
import { Container } from 'typedi';

export class IngestController {
  public encounters = Container.get(EncounterService);
  public ingest = Container.get(IngestService);

  public uploadLog = async (req: RequestWithData, res: Response, next: NextFunction) => {
    try {
      const { uploader, upload, zone } = req;
      const association = IngestService.getEncounterAssociation(zone, upload.entities);
      const existingSummary = await this.encounters.findSummaryByAssociation(association);
      if (existingSummary && Date.now() - existingSummary.created >= Pending.TIMEOUT_DURATION) {
        // Don't allow a new upload, but return the existing ID anyway
        IngestService.removeUploader(uploader.user.id);
        res.status(406).json({ code: HttpExceptionCode.TOO_OLD, id: existingSummary._id });
        return;
      }

      const id = await this.ingest.addPending(uploader, upload, association);
      res.status(200).json({ message: 'Upload pending', id: id, association: association.toString() });
    } catch (err) {
      if (err instanceof HttpException) {
        next(err);
        return;
      }

      logger.error('Failed to add new pending encounter', err);
      res.status(500).json({ message: 'Failed to add new pending encounter', code: HttpExceptionCode.INTERNAL_SERVER_ERROR });
    }
  };
}
