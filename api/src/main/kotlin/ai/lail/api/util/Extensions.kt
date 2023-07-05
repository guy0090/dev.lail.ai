package ai.lail.api.util

import ai.lail.api.data.users.AuthUser
import ai.lail.api.security.SecurityConstants.AUTH_USER
import jakarta.servlet.http.HttpServletRequest

fun HttpServletRequest.getAuthUser(): AuthUser? {
    return this.getAttribute(AUTH_USER) as? AuthUser
}

fun HttpServletRequest.getRateLimitKey(): String {
    return this.getAuthUser().let { authUser ->
        if (authUser == null) {
            "${this.requestURI}|${this.remoteAddr}"
        } else {
            "${this.requestURI}|${authUser.user.id}"
        }
    }
}