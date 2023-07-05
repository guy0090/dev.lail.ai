package ai.lail.api.repositories

import ai.lail.api.data.encounters.summaries.EncounterSummary
import ai.lail.api.enums.PrivacySetting
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface EncounterSummaryRepository : MongoRepository<EncounterSummary, String> {

    @Aggregation(
        pipeline = [
            "{\$match: {id: ?0, visibility: { \$in: ?1 }}}",
        ]
    )
    fun getSummary(id: String, privacy: List<PrivacySetting>): Optional<EncounterSummary>

    @Aggregation(
        pipeline = [
            "{\$match: {id: {\$in: ?0}, visibility: { \$in: ?1 }}}",
        ]
    )
    fun getSummaries(ids: List<String>, privacy: List<PrivacySetting>): List<EncounterSummary>

    @Aggregation(
        pipeline = [
            "{\$match: {id: {\$ne: ?0}, \"participants.partyId\": {\$in: ?1}}}",
            "{\$sort: {\"association.zone\": 1}}"
        ]
    )
    fun findRelatedByPartyIds(encounterId: String, partyIds: List<String>): List<EncounterSummary>

    @Aggregation(
        pipeline = [
            "{\$match: {\"association.zone\": ?0}}",
            "{\$skip: ?1}",
            "{\$limit: ?2}"
        ]
    )
    fun findByAssociationZone(zone: Int, skip: Int = 0, limit: Int = 20): List<EncounterSummary>

    @Aggregation(
        pipeline = [
            "{\$match: {visibility: { \$in: ?2 }}}",
            "{\$sort: {created: -1}}",
            "{\$skip: ?0}",
            "{\$limit: ?1}"
        ]
    )
    fun findLatest(skip: Int, limit: Int, privacy: List<PrivacySetting>): List<EncounterSummary>

    @Aggregation(
        pipeline = [
            "{\$match: {created: {\$gte: ?0}, visibility: {\$in: ?1 }}}",
            "{\$sort: {\"participants.dps\": -1}}",
            "{\$limit: 1}"
        ]
    )
    fun findHighestDpsSince(timestamp: Long, privacy: List<PrivacySetting>): List<EncounterSummary>

    @Aggregation(
        pipeline = [
            "{\$match: {owner: ?0}}",
            "{\$count: \"count\"}"
        ]
    )
    fun countByOwner(owner: ObjectId): Optional<Int>

    @Aggregation(
        pipeline = [
            "{\$match: {owner: ?0}}",
            "{\$sort: {created: -1}}",
            "{\$limit: 1}"
        ]
    )
    fun findLatestByOwner(owner: ObjectId): List<EncounterSummary>
}