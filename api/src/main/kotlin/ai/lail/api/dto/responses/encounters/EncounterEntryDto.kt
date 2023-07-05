package ai.lail.api.dto.responses.encounters

import ai.lail.api.data.encounters.summaries.EncounterSummary

data class EncounterEntryDto(
    var summary: EncounterSummary,
    var encounter: LinkedHashMap<String, Any>,
    var related: List<EncounterSummary>? = null
)