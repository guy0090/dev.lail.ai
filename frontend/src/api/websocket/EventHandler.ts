import type { EventBody } from '@/api/websocket/messages/Messages';
import { WebSocketResponse } from './commands/CommandResponses'
import { WebSocketEvent } from './EventTypes'
import * as Messages from '@/api/websocket/messages/Messages'
import { AppException, AppExceptionCode } from '@/common/AppException';

/**
 * Handles an incoming websocket event, returning the event body or a response.
 *
 * Return type is dependent on the event type. If the event is a response to a command,
 * a {@link WebSocketResponse} is returned, which is later handled by the command that sent
 * the request. Otherwise, an {@link EventBody} is returned.
 *
 * One limitation of this approach is that we aren't statically checking the event type,
 * but instead relying on the runtime value of the event defined in {@link WebSocketEvent}.
 * This is mostly a personal choice, as I don't like the idea of having to manually
 * update this function every time a new event is added (i.e. switching through all events).
 *
 * This has one slightly annoying side effect: all messages defined in {@link Messages}
 * must have the same name as the event defined in {@link WebSocketEvent}. This is because
 * of the limitation described above.
 *
 * @throws Error if the event is not supported or invalid
 *
 * @param event - The event to handle
 * @param content - The content of the event
 * @returns The event body
 *
 * @see {@link WebSocketEvent}
 */
export const handleEvent = (event: WebSocketEvent, content: any): WebSocketResponse<any> | EventBody => {
  try {
    const allProps = Object.getOwnPropertyNames(WebSocketEvent);
    const prop: string | undefined = allProps.find(prop => {
      const descriptor = Object.getOwnPropertyDescriptor(WebSocketEvent, prop)
      return descriptor?.value === event
    })

    if (!prop) throw new AppException(AppExceptionCode.UnsupportedSocketEvent)
    else if (prop.toLowerCase() === "response" || prop.toLowerCase() === "error") return new WebSocketResponse(content)
    else return new (Messages as { [key: string]: any })[prop](content)
  } catch (err: any) {
    console.error(event, err.message)
    throw new AppException(AppExceptionCode.InvalidSocketEvent)
  }
}
