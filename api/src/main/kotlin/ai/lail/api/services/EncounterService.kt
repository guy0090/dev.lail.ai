package ai.lail.api.services

import ai.lail.api.data.encounters.Encounter
import ai.lail.api.enums.PrivacySetting
import ai.lail.api.exceptions.encounters.EncounterNotFoundException
import ai.lail.api.repositories.EncounterRepository
import org.springframework.stereotype.Service

/**
 * Service for managing encounters.
 *
 * @param encounterRepository The repository to use.
 * @see EncounterRepository
 */
@Service
// @CacheConfig(cacheNames = [CacheHelper.ENCOUNTERS_CACHE])
class EncounterService(
    val encounterRepository: EncounterRepository,
) {

    fun count(): Long {
        return encounterRepository.count()
    }

    //@Cacheable(key = "#id", unless = "#result == null")
    fun getEncounter(id: String): Encounter {
        return encounterRepository.getEncounter(id).orElseThrow { EncounterNotFoundException() }
    }

    fun getEncounters(ids: List<String>): List<Encounter> {
        return encounterRepository.getEncounters(ids)
    }

    //@CacheEvict(key = "#encounterId")
    fun delete(encounterId: String) {
        encounterRepository.deleteById(encounterId)
    }
}