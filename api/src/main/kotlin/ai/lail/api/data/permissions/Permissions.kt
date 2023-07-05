package ai.lail.api.data.permissions

import ai.lail.api.data.users.User

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable

/**
 * Represents all permissions and roles a [User] has in the database.
 *
 * @property id The ID of the permissions.
 * @property permissions The permissions of the user.
 * @property roles The roles of the user.
 */
@Document("permissions")
data class Permissions(
    @Id var id: ObjectId = ObjectId(),
    var permissions: MutableSet<String> = mutableSetOf(),
    var roles: MutableSet<ObjectId> = mutableSetOf()
) : Serializable {

    /**
     * Adds a permission to the user.
     * @param toAdd The permission to add.
     * @return The current instance for chaining.
     */
    fun addPermission(toAdd: String): Permissions {
        this.permissions.add(toAdd)
        return this
    }

    /**
     * Adds permissions to the user.
     * @param toAdd The permissions to add.
     * @return The current instance for chaining.
     */
    fun addPermissions(toAdd: List<String>): Permissions {
        this.permissions.addAll(toAdd.toSet())
        return this
    }

    /**
     * Removes a permission from the user.
     * @param toRemove The permission to remove.
     * @return The current instance for chaining.
     */
    fun removePermission(toRemove: String): Permissions {
        this.permissions.remove(toRemove)
        return this
    }

    /**
     * Removes permissions from the user.
     * @param toRemove The permissions to remove.
     * @return The current instance for chaining.
     */
    fun removePermissions(toRemove: Set<String>): Permissions {
        this.permissions.removeAll(toRemove)
        return this
    }

    /**
     * Adds a role to the user.
     * @param toAdd The role to add.
     * @return The current instance for chaining.
     */
    fun addRole(toAdd: ObjectId): Permissions {
        this.roles.add(toAdd)
        return this
    }

    /**
     * Adds roles to the user.
     * @param toAdd The roles to add.
     * @return The current instance for chaining.
     */
    fun addRoles(toAdd: Set<ObjectId>): Permissions {
        this.roles.addAll(toAdd)
        return this
    }

    /**
     * Removes a role from the user.
     * @param toRemove The role to remove.
     * @return The current instance for chaining.
     */
    fun removeRole(toRemove: ObjectId): Permissions {
        this.roles.remove(toRemove)
        return this
    }

    /**
     * Removes roles from the user.
     * @param toRemove The roles to remove.
     * @return The current instance for chaining.
     */
    fun removeRoles(toRemove: Set<ObjectId>): Permissions {
        this.roles.removeAll(toRemove)
        return this
    }

    /**
     * Checks if a user has a role.
     * @param role The role as ObjectId to check for.
     * @return True if user has the role, False otherwise
     */
    fun hasRole(role: ObjectId): Boolean {
        return this.roles.contains(role)
    }
}