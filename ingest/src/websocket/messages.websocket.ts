import { WebSocketEvent } from './events.websocket';

/**
 * Base class for all incoming websocket messages (not as responses to commands).
 */
export class EventBody {
  private readonly event: WebSocketEvent;
  private readonly content: any;

  constructor(event: WebSocketEvent, content: any) {
    this.event = event;
    this.content = content;
  }

  getEvent() {
    return this.event;
  }

  getContent() {
    return this.content;
  }
}

export class Welcome extends EventBody {
  private readonly apiVersion?: string = undefined;
  private readonly encounters?: number = undefined;
  private readonly backendReady: boolean = true;

  constructor(content: any) {
    super(WebSocketEvent.Welcome, content);

    if (content.version) this.apiVersion = content.version;
    if (content.encounters !== undefined) this.encounters = content.encounters;
    if (content.backendReady !== undefined) this.backendReady = content.backendReady;
  }

  getApiVersion() {
    return this.apiVersion;
  }

  getEncounters() {
    return this.encounters;
  }

  isBackendReady() {
    return this.backendReady;
  }
}
