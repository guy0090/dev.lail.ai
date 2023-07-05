package ai.lail.api.permissions.nodes

import ai.lail.api.util.Reflect

object WebSocketPermission {
    const val ROOT = "socket"                                                       // Allows access to all socket nodes
    const val SELF = "socket.self"                                                  // Allows access to self socket nodes
    const val SELF_NOTIFICATIONS = "$SELF.notifications"                            // Allows access to all self notification socket nodes
    const val SELF_NOTIFICATIONS_CLEAR = "$SELF_NOTIFICATIONS.clear"                // Allows user to clear their own notification
    const val SELF_NOTIFICATIONS_VIEW = "$SELF_NOTIFICATIONS.view"                  // Allows user to get their own notifications
    const val SELF_FOLLOWERS = "$SELF.followers"                                    // Allows access to all self follower socket nodes
    const val SELF_FOLLOW_USER = "$SELF_FOLLOWERS.follow"                           // Allows user to follow other users
    const val SELF_UNFOLLOW_USER = "$SELF_FOLLOWERS.unfollow"                       // Allows user to unfollow other users
    const val SELF_FOLLOWING = "$SELF_FOLLOWERS.following"                          // Allows access to all self following socket nodes
    const val USER = "socket.user"                                                  // Allows access to user socket nodes
    const val USER_FOLLOWERS = "$USER.followers"                                    // Allows access to all user follower socket nodes
    const val USER_FOLLOWERS_COUNT = "$USER_FOLLOWERS.count"                        // Allows access to the amount of followers a user has
    // Administration permissions
    const val MANAGE = "$ROOT.manage"                                               // Allows access to all admin socket nodes
    const val MANAGE_NOTIFICATIONS = "$MANAGE.notifications"                        // Allows access to all admin notification socket nodes
    const val MANAGE_NOTIFICATIONS_CLEAR = "$MANAGE_NOTIFICATIONS.clear"            // Allows access to clear any user's notifications via WebSocket
    const val MANAGE_FOLLOWERS = "$MANAGE.followers"                                // Allows access to all admin followers socket nodes
    const val MANAGE_FOLLOW_USER = "$MANAGE_FOLLOWERS.follow"                       // Allows access to force a user to follow another user via WebSocket
    const val MANAGE_UNFOLLOW_USER = "$MANAGE_FOLLOWERS.unfollow"                   // Allows access to force a user to unfollow another user via WebSocket
    const val MANAGE_USER = "$MANAGE.user"                                          // Allows access to all admin user socket nodes
    const val MANAGE_USER_DETAILS = "$MANAGE_USER.details"                          // Allows access to view any user's extended profile via WebSocket
    const val MANAGE_USER_ROLES = "$MANAGE_USER.roles"                              // Allows access to manage any user's roles
    const val MANAGE_USER_ROLES_ADD = "$MANAGE_USER_ROLES.add"                      // Allows access to add a role of any user
    const val MANAGE_USER_ROLES_REMOVE = "$MANAGE_USER_ROLES.remove"                // Allows access to remove a role of any user
    const val MANAGE_USER_PERMISSIONS = "$MANAGE_USER.permissions"                  // Allows access to manage any user's permissions
    const val MANAGE_USER_PERMISSIONS_VIEW = "$MANAGE_USER_PERMISSIONS.view"        // Allows access to view any user's permissions
    const val MANAGE_USER_PERMISSIONS_ADD = "$MANAGE_USER_PERMISSIONS.add"          // Allows access to add a permission of any user
    const val MANAGE_USER_PERMISSIONS_REMOVE = "$MANAGE_USER_PERMISSIONS.remove"    // Allows access to remove a permission of any user
    const val MANAGE_ROLES = "$MANAGE.roles"                                        // Allows access to all admin role socket nodes
    const val MANAGE_ROLES_CREATE = "$MANAGE_ROLES.create"                          // Allows access to create a new role via WebSocket
    const val MANAGE_ROLES_DELETE = "$MANAGE_ROLES.delete"                          // Allows access to delete a role via WebSocket
    const val MANAGE_COLLECTION = "$MANAGE.collection"                              // Allows access to all admin MongoDB repository socket nodes
    const val MANAGE_COLLECTION_COUNT = "$MANAGE_COLLECTION.count"                  // Allows access to retrieve the number of entries for encounters, users, and roles collection
    const val MANAGE_EVENTS = "$MANAGE.events"                                      // Allows access to all admin event socket nodes
    const val MANAGE_EVENTS_PUBLISH = "$MANAGE_EVENTS.publish"                      // Allows access to publish events via WebSocket (remotely)
    const val MANAGE_SYSTEM = "$MANAGE.system"                                      // Allows access to all admin system socket nodes
    const val MANAGE_SYSTEM_SETTINGS = "$MANAGE_SYSTEM.settings"                    // Allows access to all admin system settings socket nodes
    const val MANAGE_SYSTEM_SETTINGS_VIEW = "$MANAGE_SYSTEM_SETTINGS.view"          // Allows access to view any system setting via WebSocket
    // TODO: Add individual permissions per event?

    val defaults = listOf(SELF, USER_FOLLOWERS_COUNT)

    fun getAllPermissions(): Set<String> = Reflect.getAllConstValues(this::class)

    fun isValidPermission(permission: String): Boolean = getAllPermissions().contains(permission)

    fun areValidPermissions(permissions: List<String>): Boolean = getAllPermissions().containsAll(permissions)
}