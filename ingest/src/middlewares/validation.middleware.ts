import { plainToInstance } from 'class-transformer';
import { validateOrReject, ValidationError } from 'class-validator';
import { NextFunction, Response } from 'express';
import { HttpException, HttpExceptionCode } from '@exceptions/httpException';
import { logger } from '@/utils/logger';
import { RequestWithData } from '@/interfaces/auth.interface';
import { UploadDto } from '@/dtos/encounter.dto';
import { IngestService } from '@/services/ingest.service';

/**
 * @name UploadValidationMiddleware
 * @description Validates a UploadDto
 * @param type dto The DTO to validate against
 * @param skipMissingProperties Wether or not to skip missing properties
 * @param whitelist Wether or not to remove non-decorated properties
 * @param forbidNonWhitelisted Wether or not to throw an error on non-decorated properties
 */
export const UploadValidationMiddleware = (skipMissingProperties = false, whitelist = true, forbidNonWhitelisted = false) => {
  return (req: RequestWithData, _res: Response, next: NextFunction) => {
    const dto = plainToInstance(UploadDto, req.body);
    delete req.body;

    validateOrReject(dto, { skipMissingProperties, whitelist, forbidNonWhitelisted })
      .then(() => {
        req.upload = dto;
        const zone = dto.postProcess();
        if (!zone?.enabled) {
          next(new HttpException(406, 'Unsupported Upload', HttpExceptionCode.UNSUPPORTED_UPLOAD));
          return;
        }
        logger.debug('Upload Validation M/W => Zone:: ' + zone.name + ' | Boss ID:: ' + dto.currentBoss);

        req.zone = zone;
        next();
      })
      .catch((errors: ValidationError[]) => {
        try {
          IngestService.removeUploader(req.uploader.user.id);
          const message = errors.map((error: ValidationError) => Object.values(error.constraints)).join(', ');
          logger.error(`Error validating request: ${message}`);
          next(new HttpException(400, message, HttpExceptionCode.INVALID_PAYLOAD));
        } catch {
          logger.error(`Error validating request: ${errors}`);
          next(new HttpException(400, 'Bad Request', HttpExceptionCode.INVALID_PAYLOAD));
        }
      });
  };
};
