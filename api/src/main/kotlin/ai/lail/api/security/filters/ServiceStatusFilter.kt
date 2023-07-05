package ai.lail.api.security.filters

import ai.lail.api.enums.ServiceStatus
import ai.lail.api.permissions.nodes.AdminPermission
import ai.lail.api.services.*
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * This filter is used to disable **all** routes prior to the service being properly started
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class ServiceStatusFilter: AbstractFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        when (adminService.getSystemStatus()) {
            ServiceStatus.READY -> filterChain.doFilter(request, response)
            ServiceStatus.STARTING -> response.sendError(503, "Service Starting")
            ServiceStatus.STOPPING -> response.sendError(503, "Service Stopping")
            ServiceStatus.UNKNOWN -> response.sendError(500, "Service in Unknown State")
            ServiceStatus.MAINTENANCE -> {
                if (canBypassMaintenance(request)) filterChain.doFilter(request, response)
                else response.sendError(503, "Service in Maintenance")
            }

            else -> response.sendError(503, "Service in Unexpected State")
        }
    }

    fun canBypassMaintenance(request: HttpServletRequest): Boolean {
        try {
            val authCookie = request.cookies?.find {
                it.name == jwtService.getPrincipalName()
            } ?: return false
            jwtService.verifyToken(authCookie.value).let {
                if (it == null) return false
                return permissionService.hasPermission(it.id, AdminPermission.MAINTENANCE)
            }
        } catch (e: Exception) {
            // TODO: Log this
            return false
        }
    }

}