package ai.lail.api.services

import ai.lail.api.data.encounters.summaries.EncounterSummary
import ai.lail.api.enums.PrivacySetting
import ai.lail.api.exceptions.encounters.EncounterNotFoundException
import ai.lail.api.repositories.EncounterSummaryRepository
import ai.lail.api.util.CacheHelper
import org.bson.types.ObjectId
import org.springframework.cache.annotation.CacheConfig
import org.springframework.stereotype.Service


/**
 * Service for managing encounter summaries via. MongoDB.
 */
@Service
@CacheConfig(cacheNames = [CacheHelper.ENCOUNTER_SUMMARIES_CACHE])
class EncounterSummaryService(
    val encounterSummaryRepository: EncounterSummaryRepository,
    val encounterService: EncounterService,
    val permissionService: PermissionService,
) {

    fun count(): Long {
        return encounterSummaryRepository.count()
    }

    @Throws(EncounterNotFoundException::class)
    fun getSummary(id: String, privacy: List<PrivacySetting>): EncounterSummary {
        return encounterSummaryRepository.getSummary(id, privacy).orElseThrow { EncounterNotFoundException() }
    }

    fun getSummaries(ids: List<String>, privacy: List<PrivacySetting>): List<EncounterSummary> {
        return encounterSummaryRepository.getSummaries(ids, privacy)
    }

    fun getRelatedSummaries(encounterId: String, partyIds: List<String>): List<EncounterSummary> {
        return encounterSummaryRepository.findRelatedByPartyIds(encounterId, partyIds)
    }

    fun update(encounterSummary: EncounterSummary): EncounterSummary {
        return encounterSummaryRepository.save(encounterSummary)
    }

    fun delete(id: String) {
        encounterSummaryRepository.deleteById(id)
        encounterService.delete(id)
    }

    fun delete(id: ObjectId) = delete(id.toHexString())

    fun getByZoneId(zoneId: Int, limit: Int = 100): List<EncounterSummary> {
        return encounterSummaryRepository.findByAssociationZone(zoneId, limit)
    }

    /**
     * Get the latest [limit] encounter summaries. Pagination is supported via. the [page] parameter.
     *
     * @param page The page to return.
     * @param limit The number of encounters to return.
     * @param privacy The privacy settings to filter by.
     *
     * @return The list of encounter summaries.
     */
    fun getLatest(
        page: Int = 0, limit: Int = 20, privacy: List<PrivacySetting>
    ): List<EncounterSummary> {
        val skip = page * limit
        return encounterSummaryRepository.findLatest(skip, limit, privacy)
    }

    /**
     * Get the highest DPS encounter going back by the given duration in milliseconds.
     *
     * @param durationMs The duration in milliseconds to look back.
     * @param privacy The privacy settings to filter by.
     *
     * @return The highest DPS encounter summary or null if none found.
     */
    @Throws(EncounterNotFoundException::class)
    fun getHighestDpsSince(
        durationMs: Long, privacy: List<PrivacySetting>
    ): EncounterSummary? {
        val timestamp = System.currentTimeMillis() - durationMs
        return encounterSummaryRepository.findHighestDpsSince(timestamp, privacy).firstOrNull()
    }

    fun getHighestDpsToday(privacy: List<PrivacySetting>): EncounterSummary? =
        getHighestDpsSince(86400000, privacy) // 1 day

    fun countByOwner(owner: ObjectId): Int  = encounterSummaryRepository.countByOwner(owner).orElse(0)

    fun getLatestSummaryByOwner(owner: ObjectId): EncounterSummary? =  encounterSummaryRepository.findLatestByOwner(owner).firstOrNull()
}