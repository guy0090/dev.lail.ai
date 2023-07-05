import { HttpException, HttpExceptionCode } from '@/exceptions/httpException';
import { EncounterService } from '@/services/encounters.service';
import { SystemService } from '@/services/system.service';
import { logger } from '@/utils/logger';
import { Request, Response, NextFunction } from 'express';
import Container from 'typedi';

const systemService = Container.get(SystemService);
const encounterService = Container.get(EncounterService);

export const SystemCheckMiddleware = async (_req: Request, _res: Response, next: NextFunction) => {
  try {
    const settings = await systemService.getSettings();
    if (!settings.uploadsEnabled || !settings.initDone) {
      next(new HttpException(503, 'Service is not accepting uploads', HttpExceptionCode.UPLOADING_DISABLED));
      return;
    }

    const encounters = await encounterService.countEncounters();
    if (1 + encounters >= settings.limits.maxEncounters) {
      next(new HttpException(503, 'System encounter limit reached', HttpExceptionCode.SYSTEM_ENCOUNTER_LIMIT_EXCEEDED));
      return;
    }

    next();
  } catch (error) {
    logger.error('SystemCheck M/W: Failed to check system limits: ' + error);
    next(new HttpException(500, 'Internal Server Error'));
  }
};
