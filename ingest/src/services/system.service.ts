import NodeCache from 'node-cache';
import { Service, Container } from 'typedi';
import { WebSocketService } from './websocket.service';
import { GetSystemSettingsCommand } from '@/websocket/commands.websocket';
import { SystemSettingsDto } from '@/dtos/user.dto';
import { HttpException } from '@/exceptions/httpException';
import { logger } from '@/utils/logger';

@Service()
export class SystemService {
  private static cache = new NodeCache({ stdTTL: 15, checkperiod: 1 });
  private static cacheKey = 'system:settings';
  private ws = Container.get(WebSocketService);

  public async getSettings() {
    const cached = SystemService.cache.get<SystemSettingsDto>(SystemService.cacheKey);
    if (cached) return cached;

    const command = new GetSystemSettingsCommand();
    const result = await this.ws.sendCommand(command);
    if (result.error || !result.data) {
      logger.error(`Failed to get system settings:: [${result.error.code}] ${result.error.message}`);
      throw new HttpException(400, 'Failed to get system settings');
    }

    const settings = command.getResult(result.data).result;
    SystemService.cache.set(SystemService.cacheKey, settings);
    return settings;
  }
}
