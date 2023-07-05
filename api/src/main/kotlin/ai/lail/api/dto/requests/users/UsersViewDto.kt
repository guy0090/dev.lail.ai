package ai.lail.api.dto.requests.users

import ai.lail.api.enums.PrivacySetting
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class UsersViewDto {
    @NotNull(message = "You must provide an argument.")
    @NotEmpty(message = "You must provide at least one user ID.")
    val userIds: List<@Valid @Pattern(regexp = "^[a-zA-Z0-9]{24}$") String> = emptyList()

    @Size(min = 1, max = 3, message = "You must supply at least one privacy setting (max. 3).")
    val privacy: List<PrivacySetting> = listOf(PrivacySetting.PUBLIC, PrivacySetting.UNLISTED)
}