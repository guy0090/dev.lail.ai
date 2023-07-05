import { v4 as uuid } from 'uuid'
import type { Notification } from '@/api/common/Notification'
import * as Data from './CommandResponses'

/// Commands ///
export enum Command {
  ClearNotification = 2000,
  GetNotifications = 2001,
  ClearNotifications = 2002,
  ClearAllNotifications = 2003,
  ForceClearNotifications = 2004,
  ForceClearAllNotifications = 2005,
  SetNotificationSeen = 2006,
  FollowUser = 2007,
  UnfollowUser = 2008,
  GetFollowing = 2009,
  GetFollowers = 2010,
  ForceFollowUser = 2011,
  ForceUnfollowUser = 2012,
  AddRoleToUser = 2013,
  RemoveRoleFromUser = 2014,
  ChangeUserDetails = 2020
  // Add more commands here
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
export abstract class WebSocketCommand<Data extends Data.WebSocketResponseData> {
  private resultClass: { new (data: any): Data }
  eventId: number
  assoc: string
  content?: any

  constructor(command: Command, resultClass: { new (data: any): Data }) {
    this.resultClass = resultClass
    this.eventId = command
    this.assoc = uuid()
  }

  toJSON(): { event: number; content: any; assoc: string } {
    return {
      event: this.eventId,
      content: this.content || {},
      assoc: this.assoc
    }
  }

  getResult(result: any): Data {
    return new this.resultClass(result)
  }
}

/**
 * Command to clear a notification.
 * @param notification The notification to clear.
 */
export class ClearNotificationCommand extends WebSocketCommand<Data.ClearNotificationData> {
  constructor(notification: Notification) {
    super(Command.ClearNotification, Data.ClearNotificationData)
    this.content = { notificationId: notification.id }
  }
}

/**
 * Command to get the current user's notifications.
 */
export class GetNotificationsCommand extends WebSocketCommand<Data.GetNotificationsData> {
  constructor() {
    super(Command.GetNotifications, Data.GetNotificationsData)
  }
}

/**
 * Command to clear multiple notifications.
 * @param notifications The notifications to clear.
 */
export class ClearNotificationsCommand extends WebSocketCommand<Data.ClearNotificationsData> {
  constructor(notifications: Notification[]) {
    super(Command.ClearNotifications, Data.ClearNotificationsData)
    this.content = { notificationIds: notifications.map((n) => n.id) }
  }
}

/**
 * Command to clear all notifications.
 */
export class ClearAllNotificationsCommand extends WebSocketCommand<Data.ClearAllNotificationsData> {
  constructor() {
    super(Command.ClearAllNotifications, Data.ClearAllNotificationsData)
  }
}

/**
 * Command to forcibly clear a notification for a user.
 * @param targetId The ID of the user to clear the notification for.
 * @param notification The notification to clear.
 */
export class ForceClearNotificationsCommand extends WebSocketCommand<Data.ForceClearAllNotificationsData> {
  constructor(targetId: string, notifications: Notification[]) {
    super(Command.ForceClearNotifications, Data.ForceClearAllNotificationsData)
    this.content = {
      targetId,
      notificationIds: notifications.map((n) => n.id)
    }
  }
}

/**
 * Command to forcibly clear all notifications for a user.
 * @param targetId The ID of the user to clear the notifications for.
 */
export class ForceClearAllNotificationsCommand extends WebSocketCommand<Data.ForceClearAllNotificationsData> {
  constructor(targetId: string) {
    super(Command.ForceClearAllNotifications, Data.ForceClearAllNotificationsData)
    this.content = { targetId }
  }
}

/**
 * Command to set a notification as seen.
 * @param notificationId The ID of the notification to set as seen.
 */
export class SetNotificationSeenCommand extends WebSocketCommand<Data.SetNotificationSeenData> {
  constructor(notificationId: string) {
    super(Command.SetNotificationSeen, Data.SetNotificationSeenData)
    this.content = { targetId: notificationId }
  }
}

/**
 * Command to follow a user.
 * @param targetId The ID of the user to follow.
 */
export class FollowUserCommand extends WebSocketCommand<Data.FollowUserData> {
  constructor(targetId: string) {
    super(Command.FollowUser, Data.FollowUserData)
    this.content = { targetId }
  }
}

/**
 * Command to unfollow a user.
 * @param targetId The ID of the user to unfollow.
 */
export class UnfollowUserCommand extends WebSocketCommand<Data.UnfollowUserData> {
  constructor(targetId: string) {
    super(Command.UnfollowUser, Data.UnfollowUserData)
    this.content = { targetId }
  }
}

/**
 * Command to get the users that the current user is following.
 */
export class GetFollowingCommand extends WebSocketCommand<Data.GetFollowingData> {
  constructor() {
    super(Command.GetFollowing, Data.GetFollowingData)
  }
}

/**
 * Command to get the number of users that are following a user.
 * @param targetId The ID of the user to get the number of followers for.
 */
export class GetFollowersCommand extends WebSocketCommand<Data.GetFollowersData> {
  constructor(targetId: string) {
    super(Command.GetFollowers, Data.GetFollowersData)
    this.content = { targetId }
  }
}

/**
 * Command to force a user to follow another user.
 * @param userId The ID of the user that is being forced to follow another user.
 * @param targetId The ID of the user that is being followed
 */
export class ForceFollowUserCommand extends WebSocketCommand<Data.ForceFollowUserData> {
  constructor(userId: string, targetId: string) {
    super(Command.ForceFollowUser, Data.ForceFollowUserData)
    this.content = { userId, targetId }
  }
}

/**
 * Command to force a user to unfollow another user.
 * @param userId The ID of the user that is being forced to unfollow another user.
 * @param targetId The ID of the user that is being unfollowed
 */
export class ForceUnfollowUserCommand extends WebSocketCommand<Data.ForceUnfollowUserData> {
  constructor(userId: string, targetId: string) {
    super(Command.ForceUnfollowUser, Data.ForceUnfollowUserData)
    this.content = { userId, targetId }
  }
}

/**
 * Command to add a role to a user.
 * @param userId The ID of the user who is receiving a role.
 * @param roleId The ID of the role that is being added to the user.
 */
export class AddRoleToUserCommand extends WebSocketCommand<Data.AddRoleToUserData> {
  constructor(userId: string, roleId: string) {
    super(Command.AddRoleToUser, Data.AddRoleToUserData)
    this.content = { userId, roleId }
  }
}

/**
 * Command to remove a role from a user.
 * @param userId The ID of the user whose role is being removed.
 * @param roleId The ID of the role that is being removed from the user.
 */
export class RemoveRoleFromUserCommand extends WebSocketCommand<Data.RemoveRoleFromUserData> {
  constructor(userId: string, roleId: string) {
    super(Command.RemoveRoleFromUser, Data.RemoveRoleFromUserData)
    this.content = { userId, roleId }
  }
}
