package ai.lail.api.dto.responses.encounters

import ai.lail.api.data.encounters.summaries.EncounterSummary
import java.io.Serializable

data class RecentEncountersDto(
    val top: EncounterSummary?,
    val recents: List<EncounterSummary>,
) : Serializable