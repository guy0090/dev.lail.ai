package ai.lail.api.data.users

data class AuthUser(var user: User, var permissions: Set<String> = setOf())