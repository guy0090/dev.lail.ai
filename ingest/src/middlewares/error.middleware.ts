import { NextFunction, Response } from 'express';
import { HttpException } from '@exceptions/httpException';
import { logger } from '@utils/logger';
import { RequestWithData } from '@/interfaces/auth.interface';
import { IngestService } from '@/services/ingest.service';

export const ErrorMiddleware = (error: HttpException, req: RequestWithData, res: Response, next: NextFunction) => {
  try {
    const status: number = error.status || 500;
    const message: string = error.message || 'Something went wrong';
    const id: string = error.id;
    const code: number = error.code;

    let errorLog = `[${req.method} ${status}/${code}] ${id} | ${req.path} >> Message:: ${message}`;
    if (req.uploader) {
      const user = req.uploader.user;
      setTimeout(() => {
        const removed = IngestService.removeUploader(user.id);
        if (removed) {
          logger.debug(`Error M/W: Removed uploader (${user.id}|${user.discordUsername}) >> Error during middleware traversal`);
        }
      }, 1000);
      errorLog += `, Uploader:: ${user.id}|${user.discordUsername}#${user.discriminator}`;
    }

    // Ex: [GET 500/0] 123456789 | /api/v1/ingest >> Message:: Something went wrong
    logger.error(errorLog);
    res.status(status).json({ status, request: id, code, message: message });
  } catch (error) {
    next(error);
  }
};
