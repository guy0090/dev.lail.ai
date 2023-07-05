import CronJob from 'node-cron';
import { logger } from '@utils/logger';
import { VaultService } from '@/services/vault.service';
import { IngestService } from '@/services/ingest.service';
import { Pending } from '@/data/ingest.data';

export const initCronJobs = () => {
  const scheduledLeaseRenew = CronJob.schedule('0 */1 * * *', () => {
    VaultService.renewLease()
      .then(res => {
        logger.info(`Vault lease renewed, now valid for ${res.lease_duration / 3600} hours`);
      })
      .catch(error => logger.error(`Error renewing lease: ${error.message}`));
  });

  /**
   * Running every minute is fine in this case because the only way to even get
   * to this point is if an unhandled error is thrown in auth middleware.
   */
  const scheduledUploaderTimeout = CronJob.schedule('*/1 * * * *', () => {
    const uploaders = IngestService.getUploaders();
    const maxPendingDuration = Pending.TIMEOUT_DURATION;
    for (const [id, timestamp] of uploaders) {
      if (Date.now() - timestamp > maxPendingDuration * 2) {
        logger.info(`CronJob: Removing residual uploader ${id} `);
        IngestService.removeUploader(id);
      }
    }
  });

  scheduledLeaseRenew.start();
  scheduledUploaderTimeout.start();
};
