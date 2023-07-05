package ai.lail.api.security.filters

import ai.lail.api.annotations.ControllerPermission
import ai.lail.api.annotations.RoutePermission
import ai.lail.api.data.users.AuthUser
import ai.lail.api.security.SecurityConstants.AUTH_USER
import ai.lail.api.services.JwtService
import ai.lail.api.services.PermissionService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerExecutionChain
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method
import java.util.*

/**
 * Filter used to check if user has a node permission required for the endpoint being accessed.
 * The filter will check the permission annotation of the controller method and the controller class,
 * see [getHandlerMethod], [getRoutePermissionAnnotation] and [getRouteParentPermissionAnnotation].
 *
 * Additionally, some endpoints have optional permissions. In those cases, validation may also fail
 * without the request being rejected.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 3)
class PermissionFilter :
    AbstractFilter() {

    @Autowired
    lateinit var appContext: ApplicationContext

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.debug("PermissionFilter.doFilterInternal() - Request: ${request.method} ${request.requestURI}")
        if (request.method == "OPTIONS") { // CORS preflight
            filterChain.doFilter(request, response)
            return
        }

        try {
            val handler = getHandlerMethod(request, appContext)
            if (handler == null && !request.requestURI.startsWith("/events")) {
                logger.warn("PermissionFilter.doFilterInternal() - Handler is null >> ${request.requestURI}") // If a non-mapped route is accessed, the handler will be null
                response.sendError(500, "Internal server error")
                return
            }

            val routePermission = handler?.let { getPermission(it) }
            val authUser = getAuthentication(request)
            if (routePermission == null) {
                if (authUser != null) {
                    logger.debug("PermissionFilter.doFilterInternal() - Optional auth validated for ${request.method} ${request.requestURI}")
                    request.setAttribute(AUTH_USER, authUser)
                }

                filterChain.doFilter(request, response)
                return
            }

            if (authUser == null) {
                logger.error("PermissionFilter.doFilterInternal() - User is not authenticated")
                response.sendError(401, "User is not authenticated")
                return
            }

            val hasAccess = permissionService.hasPermission(routePermission, authUser.permissions)
            if (!hasAccess) {
                logger.error("PermissionFilter.doFilterInternal() - User (${authUser.user.id}) does not have permission $routePermission")
                response.sendError(403, "User does not have permission $routePermission")
                return
            }
            request.setAttribute(AUTH_USER, authUser)
        } catch (e: Exception) {
            logger.error("PermissionFilter.doFilterInternal() - Exception: ${e.message}")
            response.sendError(500, "Internal server error")
            return
        }
        filterChain.doFilter(request, response)
    }

    /**
     * Get the permission annotation of the route located on the controller method
     * @param handler The handler method of the route being accessed
     * @return The permission annotation of the route
     */
    private fun getRoutePermissionAnnotation(handler: Any): RoutePermission? {
        val handlerMethod: HandlerMethod = handler as HandlerMethod
        val method: Method = handlerMethod.method
        return method.getAnnotation(RoutePermission::class.java)
    }

    /**
     * Get the parent permission annotation of the route located on the controller class
     * @param handler The handler method of the route being accessed
     * @return The parent permission annotation of the route
     */
    private fun getRouteParentPermissionAnnotation(handler: Any): ControllerPermission? {
        val handlerMethod: HandlerMethod = handler as HandlerMethod
        val method: Method = handlerMethod.method
        return method.declaringClass.getAnnotation(ControllerPermission::class.java)
    }

    /**
     * Get the permission of the route being accessed based on the permission annotations.
     *
     * - If the route has a [RoutePermission] annotation, the value of the annotation will be returned.
     *     - If the value of the annotation is empty and the `inherit` flag is false, null will be returned
     *       regardless of the value of the controller permission annotation.
     *
     * - If the route has no permission annotation, the value of the controller permission annotation will be returned
     * unless that is also null, in which case null will be returned.
     *
     * @param handler The handler method of the route being accessed
     * @return The permission of the route being accessed or null if none found (no permission required)
     */
    private fun getPermission(handler: HandlerMethod): String? {
        val routePermission = getRoutePermissionAnnotation(handler)
        val controllerPermission = getRouteParentPermissionAnnotation(handler)?.value

        if (routePermission != null) {
            return if (routePermission.value == "" && !routePermission.inherit) null
            else routePermission.value
        }

        return controllerPermission
    }
}