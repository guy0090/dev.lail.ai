package ai.lail.api.repositories

import ai.lail.api.data.encounters.Encounter
import ai.lail.api.enums.PrivacySetting
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for managing encounters via. MongoDB.
 */
@Repository
interface EncounterRepository : MongoRepository<Encounter, String> {

    @Aggregation(
        pipeline = [
            "{\$match: {id: ?0}}",
        ]
    )
    fun getEncounter(id: String): Optional<Encounter>

    @Aggregation(
        pipeline = [
            "{\$match: {id: {\$in: ?0}}}",
        ]
    )
    fun getEncounters(ids: List<String>): List<Encounter>
}
