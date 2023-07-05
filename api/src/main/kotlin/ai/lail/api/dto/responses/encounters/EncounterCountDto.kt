package ai.lail.api.dto.responses.encounters

import ai.lail.api.data.encounters.summaries.EncounterSummary

data class EncounterCountDto(val count: Int, val latest: EncounterSummary?)