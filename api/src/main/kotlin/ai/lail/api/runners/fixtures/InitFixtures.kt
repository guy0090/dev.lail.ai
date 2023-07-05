package ai.lail.api.runners.fixtures

import ai.lail.api.data.permissions.Role
import ai.lail.api.data.users.User
import ai.lail.api.enums.UserType
import ai.lail.api.permissions.Permission
import ai.lail.api.permissions.nodes.WebSocketPermission
import org.bson.types.ObjectId

object InitFixtures {
    // Default users
    private const val DEFAULT_INGEST_USER = "000000000000000000000010"

    // Default roles
    private const val DEFAULT_MEMBER_ROLE = "000000000000000000000002"
    private const val DEFAULT_ADMIN_ROLE = "000000000000000000000003"
    private const val DEFAULT_SUPERUSER_ROLE = "000000000000000000000004"
    private const val DEFAULT_INGEST_ROLE = "000000000000000000000005"

    val defaultUserRole = Role(
        DEFAULT_MEMBER_ROLE,
        "Member",
        permissions = setOf(
            Permission.ENCOUNTER.defaults,
            Permission.USER.defaults,
            // Permission.GUILD.defaults,
            Permission.ROLE.defaults
        ).flatten().toMutableSet(),
        superUser = false,
        permanent = true,
        default = true,
        inherits = mutableSetOf(),
        icon = null,
        color = "green",
        false
    )

    val defaultAdminRole = Role(
        DEFAULT_ADMIN_ROLE,
        "Admin",
        permissions = mutableSetOf(
            Permission.ENCOUNTER.ROOT,
            Permission.ADMIN.ROOT,
            Permission.USER.ROOT,
            Permission.ROLE.ROOT,
            Permission.WEB_SOCKET.ROOT
        ),
        superUser = false,
        permanent = true,
        default = false,
        inherits = mutableSetOf(ObjectId(DEFAULT_MEMBER_ROLE)),
        icon = "mdi-shield",
        color = "red-darken-3",
        false
    )

    val defaultSuperuserRole = Role(
        DEFAULT_SUPERUSER_ROLE,
        "Superuser",
        permissions = mutableSetOf(Permission.ROOT),
        superUser = true,
        permanent = true,
        default = false,
        inherits = mutableSetOf(ObjectId(DEFAULT_ADMIN_ROLE)),
        icon = "mdi-asterisk",
        color = "purple-accent-3",
        true
    )

    val defaultIngestRole = Role(
        DEFAULT_INGEST_USER,
        "Ingest",
        permissions = mutableSetOf(
            WebSocketPermission.MANAGE_EVENTS,
            WebSocketPermission.MANAGE_USER_DETAILS,
            WebSocketPermission.MANAGE_SYSTEM_SETTINGS_VIEW
        ),
        superUser = false,
        permanent = true,
        default = false,
        inherits = mutableSetOf(ObjectId(DEFAULT_MEMBER_ROLE)),
        icon = "mdi-robot",
        color = "blue",
        true
    )

    val ingestBotUser = User(
        ObjectId(DEFAULT_INGEST_USER),
        "Ingest Service",
        userType = UserType.SYSTEM,
        maxUploads = 0
    )

}