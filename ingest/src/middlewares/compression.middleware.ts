import { HttpException } from '@/exceptions/httpException';
import { Gzip } from '@/utils/compression';
import { logger } from '@/utils/logger';
import { Response, NextFunction } from 'express';
import bytes from 'bytes';
import { UploadDto } from '@/dtos/encounter.dto';
import { RequestWithData } from '@/interfaces/auth.interface';

const CONTENT_ENCODING = 'gzip';
const INFLATED_CONTENT_LENGTH = 'x-inflated-length';
const MAX_CONTENT_LENGTH = bytes('5mb'); // TODO: less?

export const gzipDecompress = async (req: RequestWithData, res: Response, next: NextFunction) => {
  try {
    if (!req.body) {
      logger.debug(`Compression M/W: Request body is empty`);
      next(new HttpException(400, 'Bad Request'));
      return;
    }

    if (!req.headers['content-encoding'] || req.headers['content-encoding'] !== CONTENT_ENCODING) {
      logger.debug(`Compression M/W: Content encoding header is missing or doesn't match`);
      next(new HttpException(400, 'Bad Request'));
      return;
    }

    if (!req.headers['content-length'] || !req.headers[INFLATED_CONTENT_LENGTH]) {
      logger.debug(`Compression M/W: Content length header is missing`);
      next(new HttpException(411, 'Length Required'));
      return;
    }

    const contentLength = parseInt(req.headers['content-length']);
    const inflatedLength = parseInt(req.headers[INFLATED_CONTENT_LENGTH] as string);
    if (contentLength === 0) {
      logger.debug(`Compression M/W: Content length is 0`);
      next(new HttpException(400, 'Bad Request'));
      return;
    }

    if (inflatedLength > MAX_CONTENT_LENGTH || contentLength > MAX_CONTENT_LENGTH) {
      logger.debug(`Compression M/W: Content length is too large`);
      next(new HttpException(413, 'Payload Too Large'));
      return;
    }

    const buffer = [];
    req.on('data', chunk => {
      buffer.push(chunk);
    });

    req.on('end', async () => {
      const data = Buffer.concat(buffer);
      if (data.length !== contentLength) {
        logger.debug(`Compression M/W: Data length doesn't match content length`);
        next(new HttpException(400, 'Bad Request'));
        return;
      }

      Gzip.gunzip(data, inflatedLength, MAX_CONTENT_LENGTH)
        .then(decompressedData => {
          req.body = JSON.parse(decompressedData) as UploadDto;
          next();
        })
        .catch(error => {
          logger.error(`Error decompressing gzip data: ${error}`);
          next(new HttpException(500, 'Bad Entity'));
        });
    });

    req.on('error', error => {
      logger.error(`Error decompressing gzip data: ${error}`);
      next(new HttpException(500, 'Bad Entity'));
    });
  } catch (error) {
    logger.error(`Error in decompress: ${error.message}`);
    next(new HttpException(500, 'Bad Entity'));
  }
};
