import { UserDetailsDto } from '@/dtos/user.dto';
import { HttpException } from '@exceptions/httpException';
import { Types } from 'mongoose';
import NodeCache from 'node-cache';
import Container, { Service } from 'typedi';
import { WebSocketService } from './websocket.service';
import { GetUserDetailsCommand } from '@/websocket/commands.websocket';
import { logger } from '@/utils/logger';

@Service()
export class UserService {
  private static cache = new NodeCache({ stdTTL: 30, checkperiod: 1 });
  private static getCacheKey = (userId: string | Types.ObjectId) => userId + ':details';

  private ws = Container.get(WebSocketService);

  /**
   * Gets the details for a user.
   *
   * @param userId The ID of the user to get details for.
   * @returns The user's details.
   *
   * @throws HttpException if the user details could not be retrieved or the WebSocket command failed.
   */
  public async getUserDetails(userId: string | Types.ObjectId): Promise<UserDetailsDto> {
    const cacheKey = UserService.getCacheKey(userId);

    const cached = UserService.cache.get<UserDetailsDto>(cacheKey);
    if (cached) return cached;

    const command = new GetUserDetailsCommand(userId);
    const result = await this.ws.sendCommand(command);

    if (result.error || !result.data) {
      logger.error(`Failed to get user details for ${userId}:: [${result.error.code}] ${result.error.message}`);
      throw new HttpException(400, 'Failed to get user details');
    }

    const details = command.getResult(result.data).result;
    UserService.cache.set(cacheKey, details);
    return details;
  }
}
