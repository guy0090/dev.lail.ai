package ai.lail.api.data.settings.system

import ai.lail.api.runners.fixtures.InitFixtures
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

/**
 * Represents the default settings of the application in the database.
 *
 * @property defaultGuild The default guild.
 * @property defaultRole The default role.
 * @property defaultMaxUploads The default maximum number of uploads per user.
 */
data class Defaults(
    @Field("default_guild")
    var defaultGuild: ObjectId? = null,
    @Field("default_role")
    var defaultRole: ObjectId = InitFixtures.defaultUserRole.id,
    @Field("default_max_uploads")
    var defaultMaxUploads: Int = 1000
) : Serializable