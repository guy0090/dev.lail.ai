package ai.lail.api.exceptions

/**
 * Exception codes
 * - Common/system errors start at 0 and range to 999
 * - User-(settings)-related errors start at 1000 and range to 1999
 * - Role-related errors start at 2000 and range to 2999
 * - Permission-related errors start at 3000 and range to 3999
 * - Guild-related errors start at 4000 and range to 4999
 * - API Key-related errors start at 5000 and range to 5999
 * - Discord-related errors start at 6000 and range to 6999
 * - Follower-related errors start at 7000 and range to 7999
 * - Encounter-related errors start at 8000 and range to 8999
 * - Socket-related errors start at 9000 and range to 9999
 * - ... TODO: Expand when required
 */
enum class ExceptionCode(val value: Int) {
    // Common/system
    COMMON(0),
    SAVE_DATA(1),
    INVALID_OBJECT_ID(2),
    VALIDATION_FAILED(3),
    MISSING_AUTH_PRINCIPAL(4),
    SYSTEM_SETTINGS_NOT_FOUND(5),
    SIGN_UPS_DISABLED(6),

    // Users
    USER_NOT_FOUND(1000),
    USER_SETTINGS_COLLISION(1001),
    USER_SETTINGS_NOT_FOUND(1002),
    USER_ALREADY_IN_GUILD(1003),
    USER_DETAILS_CHANGE_FAILED(1004),
    USER_PROFILE_PRIVATE(1005),

    // Roles
    ROLES_DELETE_SUPER_USER_ROLE(2000),
    ROLES_DUPLICATE(2001),
    ROLES_DELETE_PERMANENT_ROLE(2002),

    // Permissions
    PERMISSIONS_NOT_FOUND(3000),
    ADD_USER_ROLE_FAILED(3001),
    REMOVE_USER_ROLE_FAILED(3002),
    ROLE_NOT_FOUND(3003),
    REMOVE_DELETED_USER_ROLE_FAILED(3004),
    PERMISSION_INVALID(3005),

    // Guilds
    GUILD_NOT_FOUND(4000),
    GUILD_ROLE_NOT_FOUND(4001),
    GUILD_MEMBER_NOT_FOUND(4002),
    GUILD_MEMBER_ALREADY_IN_GUILD(4003),
    GUILD_DELETE_PERMANENT_ROLE(4004),
    GUILD_INVALID_PERMISSIONS(4005),
    GUILD_ROLE_REMOVE(4006),
    GUILD_REMOVE_OWNER(4007),

    // API Keys
    KEY_NOT_FOUND(5000),
    KEY_NO_PERMISSIONS(5001),

    // Discord
    DISCORD_GET_GRANT(6000),
    DISCORD_GET_USER(6001),
    DISCORD_REFRESH_GRANT(6002),

    // Followers
    FOLLOWERS_NOT_FOUND(7000),
    FOLLOWERS_ALREADY_EXISTS(7001),
    FOLLOWERS_UPDATE_FAILED(7002),

    // Encounters
    ENCOUNTER_NOT_FOUND(8000),
    ENCOUNTER_UPLOAD_LIMIT(8001),
    ENCOUNTER_COMPRESSION_FAILED(8002),

    // Sockets
    INVALID_WS_CONTENT(9000),
    DUPLICATE_ASSOCIATION_ID(9001),
    MAX_PENDING_COMMANDS(9002),
    INVALID_WS_COMMAND(9003),

    // Notifications
    NOTIFICATION_NOT_FOUND(10000),
    NOTIFICATION_UPDATE_FAILED(10001)
}