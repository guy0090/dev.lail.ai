/**
 * Not actually an enum, but a class with static members.
 * TypeScript doesn't support expanding enums.
 *
 * Commands are handled differently from directly received events,
 * so they are not directly included here. Instead, they return either
 * {@link WebSocketEvent.Error} or {@link WebSocketEvent.Response} depending
 * on the success of the command.
 *
 * Events, or messages directly sent from the backend with no input from the client,
 * are defined here by their IDs. The IDs are defined in the backend and must
 * be kept in sync. The IDs range from 1000 to 1999.
 */
export class WebSocketEvent {
  private static AllValuesById: { [id: number]: WebSocketEvent } = {}

  // Reserved for responses to commands
  static readonly Error = new WebSocketEvent(-1)
  static readonly Response = new WebSocketEvent(0)
  // Incoming events
  static readonly Welcome = new WebSocketEvent(1000)
  static readonly NotificationReceived = new WebSocketEvent(1001) // A notification was sent to you
  static readonly UserDetailsChanged = new WebSocketEvent(1002) // Your user details changed

  private constructor(public readonly id: number) {
    WebSocketEvent.AllValuesById[id] = this
  }

  static fromId(id: number): WebSocketEvent {
    return WebSocketEvent.AllValuesById[id]
  }
}
