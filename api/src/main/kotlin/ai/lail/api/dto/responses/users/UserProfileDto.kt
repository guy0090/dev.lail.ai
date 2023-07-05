package ai.lail.api.dto.responses.users

data class UserProfileDto(
    val user: UserDto,
    val badges: List<Any>,
    val followers: Int = 0,
)