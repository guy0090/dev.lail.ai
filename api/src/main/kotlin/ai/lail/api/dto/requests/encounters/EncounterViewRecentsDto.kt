package ai.lail.api.dto.requests.encounters

import ai.lail.api.enums.PrivacySetting
import jakarta.validation.constraints.Size

data class EncounterViewRecentDto(
    val page: Int = 0,
    val limit: Int = 20,
    val showNames: Boolean? = null,
    @Size(max = 3)
    var privacy: List<PrivacySetting> = listOf(PrivacySetting.PUBLIC, PrivacySetting.UNLISTED)
)