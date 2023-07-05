import { randomUUID } from 'crypto';
import { GetSystemSettingsData, GetUserDetailsData, PublishPendingEncounterData, WebSocketResponseData } from './responses.websocket';
import { IEncounterSummary } from '@/data/ingest.data';
import { Types } from 'mongoose';

/// Commands ///
export enum Command {
  PUBLISH_PENDING_ENCOUNTER = 2018,
  GET_USER_DETAILS = 2019,
  GET_SYSTEM_SETTINGS = 2021,
}

/**
 * Class that all WebSocket commands should extend.
 *
 * Contains the command name and a method to serialize the command.
 *
 * Also used to deserialize the result of the command after completion.
 *
 * @param command The command to send.
 * @param resultClass The class to use to deserialize the result.
 *
 * @property resultClass The class to use to deserialize the result.
 * @property assoc A unique ID to associate the command with.
 * @property eventId The ID of the command.
 */
export abstract class WebSocketCommand<Data extends WebSocketResponseData> {
  private resultClass: { new (data: any): Data };
  eventId: number;
  assoc: string;
  content?: any;

  constructor(command: Command, resultClass: { new (data: any): Data }) {
    this.resultClass = resultClass;
    this.eventId = command;
    this.assoc = randomUUID();
  }

  toJSON(): { event: number; content: any; assoc: string } {
    return {
      event: this.eventId,
      content: this.content || {},
      assoc: this.assoc,
    };
  }

  getResult(result: any): Data {
    return new this.resultClass(result);
  }
}

/**
 * Command to notify the backend that an encounter has been uploaded.
 */
export class PublishPendingEncounterCommand extends WebSocketCommand<PublishPendingEncounterData> {
  constructor(summary: IEncounterSummary) {
    super(Command.PUBLISH_PENDING_ENCOUNTER, PublishPendingEncounterData);
    this.content = { association: summary.association.toString() };
  }
}

/**
 * Command to get a user's details
 */
export class GetUserDetailsCommand extends WebSocketCommand<GetUserDetailsData> {
  constructor(targetId: string | Types.ObjectId) {
    super(Command.GET_USER_DETAILS, GetUserDetailsData);
    this.content = { targetId };
  }
}

export class GetSystemSettingsCommand extends WebSocketCommand<GetSystemSettingsData> {
  constructor() {
    super(Command.GET_SYSTEM_SETTINGS, GetSystemSettingsData);
    this.content = {};
  }
}
