package ai.lail.api.dto.requests.encounters

import ai.lail.api.enums.PrivacySetting
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class EncounterViewDto {
    @Pattern(regexp = "^[a-zA-Z0-9]{24}$", message = "User ID must be a valid Object ID")
    var id: String = ""
    var showNames: Boolean? = null
    @Size(max = 3)
    var privacy: List<PrivacySetting> = listOf(PrivacySetting.PUBLIC, PrivacySetting.UNLISTED)
}