package ai.lail.api.dto.responses.followers

import ai.lail.api.dto.responses.users.UserDto

data class FollowersDto(val following: List<UserDto>)