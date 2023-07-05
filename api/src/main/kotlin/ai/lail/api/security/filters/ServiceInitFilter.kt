package ai.lail.api.security.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * This filter is used to disable **most** routes prior to the service
 * being properly initialized
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class ServiceInitFilter : AbstractFilter() {

    val routes = listOf(
        "/users",
        "/guilds",
        "/encounters",
        "/admin/users",
        "/admin/guilds",
        "/admin/encounters",
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val shouldFilter = !adminService.isInitialized() && routes.any { request.requestURI.startsWith(it) }
        if (shouldFilter) {
            response.sendError(503, "Service not initialized")
        } else {
            filterChain.doFilter(request, response)
        }
    }
}