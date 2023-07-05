package ai.lail.api.data.jwt

import org.bson.types.ObjectId
import java.io.Serializable

/**
 * Payload of the JWT token
 *
 * @property id User ID
 * @property hash Secure hash, consisting of the user's ID and their salt value
 */
class JwtPayload(
    val id: String = "",
    val hash: String = "",
) : Serializable {
    constructor(userId: ObjectId, secureHash: String) : this(userId.toHexString(), secureHash)
}