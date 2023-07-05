package ai.lail.api.websocket

import ai.lail.api.permissions.nodes.WebSocketPermission
import ai.lail.api.websocket.WebSocketEvent.Error
import ai.lail.api.websocket.WebSocketEvent.Response

/**
 * An enum representing all the events that can be sent over the WebSocket.
 *
 * The IDs are used to identify the event on the WebSocket. They are used to send the event
 * to the client and to identify the event when it is received.
 *
 * [id] -1 is reserved for errors
 * [id] 0 is reserved for responses
 *
 * Events that are publishable to clients are reserved to have an [id] >= 1000 and < 2000
 *
 * Some events are only used as commands sent from the client to the server. These events
 * are not sent directly to the client, but instead use either [Error] or [Response] to
 * send the result of the command to the client. Currently, response-only events are reserved
 * to have an [id] >= 2000
 *
 * Each event may have a permission. If the permission is null, the event can be received by
 * anyone. In the case that the event is a command, the permission is required to send the command
 * to the server.
 */
enum class WebSocketEvent(val id: Int, val permission: String?) {
    Error(-1, null), // Reserved for errors
    Response(0, null), // Reserved for responses

    // Outgoing events
    Welcome(1000, null),
    Notification(1001, null),
    UserDetailsChanged(1002, null),

    // Command events
    ClearNotification(2000, WebSocketPermission.SELF_NOTIFICATIONS_CLEAR),
    GetNotifications(2001, WebSocketPermission.SELF_NOTIFICATIONS_VIEW),
    ClearNotifications(2002, WebSocketPermission.SELF_NOTIFICATIONS_CLEAR),
    ClearAllNotifications(2003, WebSocketPermission.SELF_NOTIFICATIONS_CLEAR),
    ForceClearNotifications(2004, WebSocketPermission.MANAGE_NOTIFICATIONS_CLEAR),
    ForceClearAllNotifications(2005, WebSocketPermission.MANAGE_NOTIFICATIONS_CLEAR),
    SetNotificationSeen(2006, WebSocketPermission.SELF_NOTIFICATIONS_VIEW),
    FollowUser(2007, WebSocketPermission.SELF_FOLLOW_USER),
    UnfollowUser(2008, WebSocketPermission.SELF_UNFOLLOW_USER),
    GetFollowing(2009, WebSocketPermission.SELF_FOLLOWING),
    GetFollowers(2010, WebSocketPermission.USER_FOLLOWERS_COUNT),
    ForceFollowUser(2011, WebSocketPermission.MANAGE_FOLLOW_USER),
    ForceUnfollowUser(2012, WebSocketPermission.MANAGE_UNFOLLOW_USER),
    AddRoleToUser(2013, WebSocketPermission.MANAGE_USER_ROLES_ADD),
    RemoveRoleFromUser(2014, WebSocketPermission.MANAGE_USER_ROLES_REMOVE),
    CreateNewRole(2015, WebSocketPermission.MANAGE_ROLES_CREATE),
    DeleteRole(2016, WebSocketPermission.MANAGE_ROLES_DELETE),
    GetCollectionStatus(2017, WebSocketPermission.MANAGE_COLLECTION_COUNT),
    PublishPendingEncounter(2018, WebSocketPermission.MANAGE_EVENTS_PUBLISH),
    GetUserDetails(2019, WebSocketPermission.MANAGE_USER_DETAILS),
    ChangeUserDetails(2020, WebSocketPermission.MANAGE_USER_DETAILS),
    GetSystemSettings(2021, WebSocketPermission.MANAGE_SYSTEM_SETTINGS_VIEW);

    fun requiresPermission(): Boolean {
        return permission != null && permission != ""
    }

    companion object {
        fun fromId(id: Int): WebSocketEvent? {
            return values().find { it.id == id }
        }

        fun getAllPermissions(): List<String> {
            return values().mapNotNull { it.permission }
        }

        fun getCommandEvents(): List<WebSocketEvent> {
            return values().filter { it.id >= 2000 }
        }
    }
}