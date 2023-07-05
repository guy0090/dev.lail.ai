import { App } from '@/app';
import { ValidateEnv } from '@utils/validateEnv';
import { VaultService } from '@services/vault.service';
import { IngestRoute } from '@/routes/ingest.route';
import { initCronJobs } from '@utils/crons';
import { WebSocketService } from './services/websocket.service';
ValidateEnv();

VaultService.setupSecrets()
  .then(() => {
    try {
      initCronJobs();
      WebSocketService.connect();

      const app = new App([new IngestRoute()]);
      app.listen();
    } catch (err) {
      console.error('Error starting server:', err.message);
      process.exit(1);
    }
  })
  .catch(err => {
    console.error('Error getting signing key:', err.message);
    process.exit(1);
  });
