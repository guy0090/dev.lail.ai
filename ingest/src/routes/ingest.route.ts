import { IngestController } from '@/controllers/ingest.controller';
import { Routes } from '@/interfaces/routes.interface';
import { AuthMiddleware } from '@/middlewares/auth.middleware';
import { gzipDecompress } from '@/middlewares/compression.middleware';
import { SystemCheckMiddleware } from '@/middlewares/system.middleware';
import { UploadValidationMiddleware } from '@/middlewares/validation.middleware';
import { Router } from 'express';

export class IngestRoute implements Routes {
  public path = '/logs';
  public router = Router();
  public ingest = new IngestController();

  constructor() {
    try {
      this.initializeRoutes();
    } catch (err) {
      console.error('Error initializing routes:', err.message);
      process.exit(1);
    }
  }

  private initializeRoutes() {
    this.router.post(this.path, [SystemCheckMiddleware, AuthMiddleware, gzipDecompress, UploadValidationMiddleware()], this.ingest.uploadLog);
  }
}
