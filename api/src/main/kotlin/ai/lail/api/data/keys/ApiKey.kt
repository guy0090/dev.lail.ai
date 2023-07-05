package ai.lail.api.data.keys

import ai.lail.api.data.users.User
import ai.lail.api.permissions.nodes.EncounterPermission
import io.fusionauth.jwt.HexUtils
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

/**
 * Represents an API key of a [User] in the database.
 *
 * @property id The ID of the API key.
 * @property owner The ID of the owner of the API key.
 * @property name The name of the API key.
 * @property key The key of the API key.
 * @property hash The hash of the API key.
 * @property seen Whether the API key has been seen.
 * @property uses The number of times the API key has been used.
 * @property created The date the API key was created.
 * @property lastUsed The date the API key was last used.
 * @property permissions The permissions of the API key.
 *
 * @see User
 */
@Document("api_keys")
data class ApiKey(

    @Id
    var id: ObjectId = ObjectId(),
    var owner: ObjectId,
    var name: String,
    var key: String,
    var hash: String = "",
    var seen: Boolean = false,
    var uses: Int = 0,
    var created: Long = System.currentTimeMillis(),
    @Field("last_used")
    var lastUsed: Long = 0,
    var permissions: MutableSet<String> = EncounterPermission.defaults.toMutableSet()

) : Serializable {

    constructor(owner: ObjectId, name: String, key: String, hash: ByteArray) : this(
        owner = owner,
        name = name,
        key = key,
        hash = HexUtils.fromBytes(hash)
    )

    /**
     * Adds a permission to the API key.
     * @param permission The permission to add.
     */
    fun addPermission(permission: String) {
        permissions.add(permission)
    }

    /**
     * Adds permissions to the API key.
     * @param permissions The permissions to add.
     */
    fun addPermissions(permissions: List<String>) {
        this.permissions.addAll(permissions)
    }

    /**
     * Removes a permission from the API key.
     * @param permission The permission to remove.
     */
    fun removePermission(permission: String) {
        permissions.remove(permission)
    }

    /**
     * Removes permissions from the API key.
     * @param permissions The permissions to remove.
     */
    fun removePermissions(permissions: List<String>) {
        this.permissions.removeAll(permissions.toSet())
    }

    /**
     * Check if the API key has a permission.
     * @param permission The permission to check.
     */
    fun hasPermission(permission: String): Boolean {
        return permissions.contains(permission)
    }

    /**
     * Check if the API key has permissions.
     * @param permissions The permissions to check.
     */
    fun hasPermissions(permissions: List<String>): Boolean {
        return permissions.containsAll(permissions)
    }

}
