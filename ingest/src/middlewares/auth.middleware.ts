import { NextFunction, Response } from 'express';
import { verify } from 'jsonwebtoken';
import { HttpException, HttpExceptionCode } from '@exceptions/httpException';
import { RequestWithData, TokenData } from '@interfaces/auth.interface';
import { VaultService } from '@/services/vault.service';
import Container from 'typedi';
import { UserService } from '@/services/users.service';
import { UserDetailsDto } from '@/dtos/user.dto';
import { Permission } from '@/data/permissions.data';
import { logger } from '@/utils/logger';
import { IngestService } from '@/services/ingest.service';

const userService = Container.get(UserService);

const getAuthorization = (req: RequestWithData) => {
  const cookie = req.cookies['Authorization'];
  if (cookie) return cookie;

  const header = req.header('Authorization');
  if (header) return header.split('Bearer ')[1];

  return null;
};

export const AuthMiddleware = async (req: RequestWithData, _res: Response, next: NextFunction) => {
  try {
    const secretKey = VaultService.signingKey;
    if (!secretKey) {
      logger.error('Auth M/W:: Failed to get signing key from vault');
      next(new HttpException(500, 'Internal Server Error'));
      return;
    }

    const authorization = getAuthorization(req);
    if (authorization) {
      const auth = verify(authorization, secretKey) as TokenData;
      const userId = auth.payload.id;

      const details = await userService.getUserDetails(userId);
      validateUserDetails(details);
      req.uploader = details;

      if (IngestService.isUploading(userId)) {
        next(new HttpException(429, 'Too many concurrent uploads', HttpExceptionCode.TOO_MANY_CONCURRENT_UPLOADS));
        return;
      }
      IngestService.addUploader(userId);

      next();
    } else {
      next(new HttpException(403, 'Forbidden'));
    }
  } catch (error) {
    logger.error('Failed to authenticate user: ' + error);
    if (error instanceof HttpException) {
      next(error);
      return;
    }
    next(new HttpException(401, 'Invalid authentication', HttpExceptionCode.INVALID_TOKEN));
  }
};

const validateUserDetails = (details: UserDetailsDto) => {
  if (++details.user.uploads > details.user.maxUploads) {
    throw new HttpException(429, 'Upload limit exceeded', HttpExceptionCode.UPLOAD_QUOTA_EXCEEDED);
  }
  if (!hasPermission(details, Permission.CREATE_ENCOUNTER) || details.user.banned) {
    throw new HttpException(403, 'Forbidden');
  }
};

const hasPermission = (details: UserDetailsDto, permission: Permission) => {
  if (details.permissions.includes('*')) return true;
  // permission is: a.b.c.d
  // nodes are: [a, a.b, a.b.c, a.b.c.d]
  // if any of these nodes are in the permissions list, return true
  const nodes = permission.split('.');
  for (let i = 0; i < nodes.length; i++) {
    const node = nodes.slice(0, i + 1).join('.');
    if (details.permissions.includes(node)) return true;
  }
  return false;
};
