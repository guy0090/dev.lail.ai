package ai.lail.api.data.discord

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

/**
 * Represents a Discord OAuth grant associated with a user.
 * @property id The ID of the OAuth grant.
 * @property access The access token.
 * @property refresh The refresh token.
 * @property expiresAt The time at which the access token expires as a UNIX timestamp.
 * @property scope The scope of the access token.
 * @property tokenType The type of the token.
 */
@Document("discord_oauths")
data class DiscordOAuth(
    @Id
    var id: ObjectId = ObjectId(),
    var access: String = "",
    var refresh: String = "",
    @Field("expires_at") @JsonProperty("expires_at")
    var expiresAt: Long = 0L,
    var scope: String = "",
    @Field("token_type") @JsonProperty("token_type")
    var tokenType: String = ""
) : Serializable {
    companion object {

        /**
         * Creates a new grant from a Discord OAuth API response grant.
         * @param userId The ID of the user.
         * @param grant The grant.
         */
        fun fromGrant(userId: ObjectId, grant: DiscordOAuthGrant): DiscordOAuth {
            val instance = DiscordOAuth()
            instance.id = userId
            instance.access = grant.accessToken
            instance.refresh = grant.refreshToken
            instance.expiresAt = System.currentTimeMillis() + (grant.expiresIn * 1000)
            instance.scope = grant.scope
            instance.tokenType = grant.tokenType

            return instance
        }
    }
}