package ai.lail.api.repositories

import ai.lail.api.data.settings.user.UserSettings
import ai.lail.api.enums.PrivacySetting
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface UserSettingsRepository : MongoRepository<UserSettings, String> {

    @Query("{ _id: ?0 }")
    fun get(userId: ObjectId): Optional<UserSettings>

    fun findByCustomUrlSlug(urlSlug: String): Optional<UserSettings>

    @Query(
        "{ _id: { \$in: ?0 }, profilePrivacySetting: { \$in: ?1 } }"
    )
    fun getMany(userIds: Collection<ObjectId>, privacy: List<PrivacySetting>): List<UserSettings>
}