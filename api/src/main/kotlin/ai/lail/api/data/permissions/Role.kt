package ai.lail.api.data.permissions

import ai.lail.api.data.users.User
import ai.lail.api.dto.requests.roles.CreateRoleDto
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

/**
 * Represents the permissions and configuration for a [Role],
 * which any [User] can be assigned to.
 *
 * Only one [superUser] can exist at any time, and it cannot be deleted.
 *
 * [permanent] roles cannot be deleted.
 *
 * [default] roles are assigned to all new users.
 *
 * @property id The ID of the role.
 * @property name The name of the role.
 * @property permissions The permissions of the role.
 * @property superUser Whether the role is a superuser.
 * @property permanent Whether the role is permanent.
 * @property default Whether the role is the default role.
 * @property inherits The IDs of the roles this role inherits permissions from.
 * @property icon The icon of the role. See [Material Design Icons](https://pictogrammers.com/library/mdi/) for a list of icons.
 * @property color The color of the role in hex format or built-in [Vuetify Color](https://vuetifyjs.com/en/styles/colors/#material-colors) name.
 */
@Document("roles")
@JsonIgnoreProperties(ignoreUnknown = true)
data class Role(
    @Id
    val id: ObjectId = ObjectId(),
    var name: String = "",
    var permissions: MutableSet<String> = mutableSetOf(),
    @Field("superuser")
    var superUser: Boolean = false,
    var permanent: Boolean = false,
    var default: Boolean = false,
    var inherits: MutableSet<ObjectId> = mutableSetOf(),
    var icon: String? = null,
    var color: String? = null,
    val hidden: Boolean = false
) : Serializable {

    constructor(
        id: String,
        name: String,
        permissions: MutableSet<String>,
        superUser: Boolean,
        permanent: Boolean,
        default: Boolean,
        inherits: MutableSet<ObjectId>,
        icon: String?,
        color: String?,
        hidden: Boolean
    ) : this(ObjectId(id), name, permissions, superUser, permanent, default, inherits, icon, color, hidden)

    constructor(dto: CreateRoleDto) : this(
        name = dto.name,
        permissions = dto.permissions.toMutableSet(),
        inherits = dto.inherits.toMutableSet(),
        icon = dto.icon,
        color = dto.color,
        hidden = dto.hidden
    )

    /**
     * Adds a permission to this role.
     * @param toAdd The permission to add.
     * @return The current instance for chaining.
     */
    fun addPermission(toAdd: String): Role {
        this.permissions.add(toAdd)
        return this
    }

    /**
     * Adds permissions to this role.
     * @param toAdd The permissions to add.
     * @return The current instance for chaining.
     */
    fun addPermissions(toAdd: Set<String>): Role {
        this.permissions.addAll(toAdd)
        return this
    }

    /**
     * Removes a permission from this role.
     * @param toRemove The permission to remove.
     * @return The current instance for chaining.
     */
    fun removePermission(toRemove: String): Role {
        this.permissions.removeIf { it == toRemove }
        return this
    }

    /**
     * Removes permissions from this role.
     * @param toRemove The permissions to remove.
     * @return The current instance for chaining.
     */
    fun removePermissions(toRemove: Set<String>): Role {
        this.permissions.removeAll(toRemove)
        return this
    }

    /**
     * Checks if this role has a permission.
     * @param toCheck The permission to check for.
     */
    fun hasPermission(toCheck: String): Boolean {
        return this.permissions.contains(toCheck)
    }

    /**
     * Checks if this role has all the permissions.
     * @param toCheck The permissions to check for.
     */
    fun hasPermissions(toCheck: Set<String>): Boolean {
        return this.permissions.containsAll(toCheck)
    }

    /**
     * Adds a parent role to this role to inherit permissions from.
     * The role cannot be a parent of itself.
     * @param role The role to add.
     */
    fun addParent(role: Role): Role {
        if (role.id != this.id) this.inherits.add(role.id)
        return this
    }

    /**
     * Removes a parent role from this role.
     * @param role The role to remove.
     */
    fun removeParent(role: Role): Role {
        this.inherits.remove(role.id)
        return this
    }

}
