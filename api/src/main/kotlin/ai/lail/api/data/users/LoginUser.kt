package ai.lail.api.data.users

data class LoginUser(
    val user: User,
    val token: String
)