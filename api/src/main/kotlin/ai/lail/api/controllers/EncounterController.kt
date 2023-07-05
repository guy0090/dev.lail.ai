package ai.lail.api.controllers

import ai.lail.api.annotations.ControllerPermission
import ai.lail.api.annotations.RateLimit
import ai.lail.api.annotations.RoutePermission
import ai.lail.api.data.encounters.Encounter
import ai.lail.api.data.encounters.summaries.EncounterSummary
import ai.lail.api.dto.requests.encounters.EncounterViewDto
import ai.lail.api.dto.requests.encounters.EncounterViewRecentDto
import ai.lail.api.dto.responses.encounters.EncounterCountDto
import ai.lail.api.dto.responses.encounters.EncounterEntryDto
import ai.lail.api.dto.responses.encounters.RecentEncountersDto
import ai.lail.api.enums.PrivacySetting
import ai.lail.api.exceptions.encounters.EncounterCompressionException
import ai.lail.api.exceptions.encounters.EncounterNotFoundException
import ai.lail.api.permissions.nodes.EncounterPermission
import ai.lail.api.services.AdminService
import ai.lail.api.services.EncounterService
import ai.lail.api.services.EncounterSummaryService
import ai.lail.api.services.PermissionService
import ai.lail.api.util.ObjectIdHelper
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.net.HttpHeaders
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPOutputStream

/**
 * Controller for encounter related endpoints.
 *
 * Names are excluded from the [EncounterSummary] and [Encounter] by default to prevent leaking names to users without
 * the [EncounterPermission.MANAGE_VIEW] permission. To include names, set [EncounterViewDto.showNames] to true.
 *
 * @property permissionService [PermissionService] for checking permissions
 * @property encounterSummaryService [EncounterSummaryService] for getting encounter summaries
 * @property encounterService [EncounterService] for getting encounters
 */
@RestController
@RequestMapping("/encounters")
@ControllerPermission(EncounterPermission.ROOT)
@RateLimit(50)
class EncounterController(
    val permissionService: PermissionService,
    val encounterSummaryService: EncounterSummaryService,
    val encounterService: EncounterService,
    val adminService: AdminService
) {
    val logger: Logger = LoggerFactory.getLogger(EncounterController::class.java)

    @PostMapping("/count")
    @RoutePermission(inherit = false)
    fun getEncounterCount(
        @Valid @RequestBody dto: EncounterViewDto,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val userId = ObjectIdHelper.getId(dto.id)
        val count = encounterSummaryService.countByOwner(userId)
        val latest = encounterSummaryService.getLatestSummaryByOwner(userId)
        val showNames = getShowNamesChange(request, dto.showNames)
        if (!showNames) latest?.participants?.forEach { it.name = null }
        sendCompressed(response, EncounterCountDto(count, latest))
    }

    @PostMapping("/summary")
    @RoutePermission(inherit = false) // No permission required to view
    fun getSummary(
        @Valid @RequestBody dto: EncounterViewDto,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val privacy = getPrivacyChange(request, dto.privacy)
        val summary = encounterSummaryService.getSummary(dto.id, privacy)
        val showNames = getShowNamesChange(request, dto.showNames)
        if (!showNames) summary.participants.forEach { it.name = null }
        sendCompressed(response, summary)
    }

    @PostMapping("/log")
    @RoutePermission(inherit = false)
    fun getEncounter(
        @Valid @RequestBody dto: EncounterViewDto,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val privacy = getPrivacyChange(request, dto.privacy)
        encounterSummaryService.getSummary(dto.id, privacy)

        val encounter = encounterService.getEncounter(dto.id)
        val showNames = getShowNamesChange(request, dto.showNames)
        if (!showNames) removeEncounterNames(encounter)
        sendCompressed(response, encounter.data)
    }

    @PostMapping("/entry")
    @RoutePermission(inherit = false)
    fun getEntry(
        @Valid @RequestBody dto: EncounterViewDto,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val privacy = getPrivacyChange(request, dto.privacy)
        val summary = encounterSummaryService.getSummary(dto.id, privacy)
        val related = encounterSummaryService.getRelatedSummaries(dto.id, summary.getParties())
        val encounter = encounterService.getEncounter(dto.id)
        val showNames = getShowNamesChange(request, dto.showNames)
        if (!showNames) {
            related.forEach(::removeSummaryNames)
            removeSummaryNames(summary)
            removeEncounterNames(encounter)
        }
        sendCompressed(response, EncounterEntryDto(summary, encounter.data, related))
    }

    @PostMapping("/recent")
    @RoutePermission(inherit = false)
    fun getRecentSummaries(
        @Valid @RequestBody dto: EncounterViewRecentDto,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val limit = getLimitChange(request, dto.limit)
        val privacy = getPrivacyChange(request, dto.privacy)
        val top = encounterSummaryService.getHighestDpsToday(privacy)
        val recent = encounterSummaryService.getLatest(0, limit, privacy)
        val showNames = getShowNamesChange(request, dto.showNames)
        if (!showNames) {
            top?.participants?.forEach { it.name = null }
            recent.forEach { summary ->
                summary.participants.forEach { it.name = null }
            }
        }
        sendCompressed(response, RecentEncountersDto(top, recent))
    }

    @PostMapping("/recent/top")
    @RoutePermission(inherit = false)
    fun getTopDpsToday(
        @Valid @RequestBody dto: EncounterViewRecentDto,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val privacy = getPrivacyChange(request, dto.privacy)
        val summary = encounterSummaryService.getHighestDpsToday(privacy) ?: throw EncounterNotFoundException()
        val showNames = getShowNamesChange(request, dto.showNames)
        if (!showNames) removeSummaryNames(summary)
        sendCompressed(response, summary)
    }

    @PostMapping("/recent/latest")
    @RoutePermission(inherit = false)
    fun getLatestUploads(
        @Valid @RequestBody dto: EncounterViewRecentDto,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val limit = getLimitChange(request, dto.limit)
        val privacy = getPrivacyChange(request, dto.privacy)
        val summaries = encounterSummaryService.getLatest(dto.page, limit, privacy)

        if (summaries.isEmpty()) return
        val showNames = getShowNamesChange(request, dto.showNames)
        if (!showNames) summaries.forEach { summary ->
            summary.participants.forEach { it.name = null }
        }

        sendCompressed(response, summaries)
    }

    @PostMapping("/filter")
    @RoutePermission("filter")
    fun getEncountersByFilter(@RequestBody filter: Any): List<Encounter> {
        return listOf()
    }

    /// Misc. helpers

    private fun getShowNamesChange(request: HttpServletRequest, showNames: Boolean?): Boolean {
        val defaultShowNames = adminService.getSettings().showPlayerNames
        return permissionService.permittedChange(
            EncounterPermission.MANAGE_VIEW, request, showNames, defaultShowNames
        )
    }

    private fun getPrivacyChange(request: HttpServletRequest, privacy: List<PrivacySetting>): List<PrivacySetting> {
        val defaultPrivacySetting = listOf(PrivacySetting.PUBLIC, PrivacySetting.UNLISTED)
        return permissionService.permittedChange(
            EncounterPermission.MANAGE_VIEW, request, privacy, defaultPrivacySetting
        )
    }

    private fun getLimitChange(request: HttpServletRequest, limit: Int?): Int {
        val defaultLimit = 20
        return permissionService.permittedChange(
            EncounterPermission.MANAGE_VIEW, request, limit, defaultLimit
        )
    }

    private fun removeSummaryNames(summary: EncounterSummary) = summary.participants.forEach { it.name = null }

    private fun removeEncounterNames(encounter: Encounter) = encounter.data["entities"].let {
        if (it is List<*>) {
            val entities = it as List<LinkedHashMap<String, Any>>
            entities.forEach { entity ->
                entity.remove("name")
            }
        }
    }

    private fun sendCompressed(response: HttpServletResponse, data: Any) {
        try {
            val jsonString = ObjectMapper().writer().writeValueAsString(data)
            response.setHeader(HttpHeaders.CONTENT_ENCODING, "gzip")
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.displayName()

            val gzip = GZIPOutputStream(response.outputStream)
            gzip.use {
                it.write(jsonString.toByteArray(StandardCharsets.UTF_8))
                it.finish()
            }
        } catch (e: Exception) {
            throw EncounterCompressionException(e)
        }
    }
}