import { Service } from 'typedi';
import WebSocket from 'ws';
import { API_HOST, API_PORT } from '@/config';
import { VaultService } from './vault.service';
import { logger } from '@/utils/logger';
import { DeferredResponse, WebSocketResponse, WebSocketResponseData } from '@/websocket/responses.websocket';
import { WebSocketEvent, handleEvent } from '@/websocket/events.websocket';
import { WebSocketCommand } from '@/websocket/commands.websocket';

@Service()
export class WebSocketService {
  private static socket: WebSocket;
  private static connected = false;
  private static reconnecting = false;
  private static retryInterval = 10000;
  private static retryTimer: ReturnType<typeof setInterval> | null = null;
  private static pending = new Map<string, DeferredResponse>();

  public static async connect() {
    if (this.connected) return;

    const token = await VaultService.getSocketToken();
    this.socket = new WebSocket(`ws://${API_HOST}:${API_PORT}/events?token=${token}`);

    this.socket.onopen = () => {
      this.connected = true;
      if (this.reconnecting && this.retryTimer != null) {
        this.reconnecting = false;
        clearInterval(this.retryTimer);
        this.retryTimer = null;
      }
      logger.info('WebSocket connected: ' + this.socket.url.split('?token')[0]);
    };

    this.socket.onclose = event => {
      logger.warn('WebSocket disconnected => ' + event.reason + ' (' + event.code + ')');
      this.connected = false;
      if (event.code !== 1000 && !this.reconnecting) {
        // Backend shut down or restarted
        logger.warn('WebSocket: Disconnected, attempting to reconnect...');
        this.reconnecting = true;
        if (this.retryTimer === null) {
          this.retryTimer = setInterval(() => {
            this.reconnect();
          }, this.retryInterval);
        }
      }
    };

    this.socket.onerror = error => {
      logger.error('WebSocket error: ' + error.message);
    };

    this.socket.onmessage = msg => {
      try {
        const data = JSON.parse(msg.data.toString('utf-8'));
        const event = WebSocketEvent.fromId(data.event);
        const content = handleEvent(event, data.content);

        if (content instanceof WebSocketResponse) {
          const deferral = this.pending.get(content.assoc);
          if (deferral) {
            deferral.resolve(content);
            this.pending.delete(content.assoc);
          }
        } else {
          logger.debug('Unhandled WebSocket message: ' + JSON.stringify(data));
        }
      } catch (err: any) {
        console.error('Error parsing websocket message: ', err.message);
      }
    };
  }

  public static disconnect() {
    if (!this.connected) return;
    this.socket.close();
  }

  private static reconnect() {
    this.disconnect();
    setTimeout(() => {
      this.connect();
    }, 100);
  }

  public sendCommand<T extends WebSocketResponseData>(command: WebSocketCommand<T>, timeout = 5000): Promise<WebSocketResponse<T>> | undefined {
    if (!WebSocketService.connected) return undefined;

    WebSocketService.pending.set(command.assoc, new DeferredResponse<T>());
    setTimeout(() => {
      const deferral = WebSocketService.pending.get(command.assoc);
      if (deferral) {
        deferral.reject(new Error('Request timed out'));
        WebSocketService.pending.delete(command.assoc);
      }
    }, timeout);

    WebSocketService.socket.send(JSON.stringify(command));
    return WebSocketService.pending.get(command.assoc).deferral;
  }
}
