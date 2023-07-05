package ai.lail.api.security.filters

import ai.lail.api.data.users.AuthUser
import ai.lail.api.services.AdminService
import ai.lail.api.services.JwtService
import ai.lail.api.services.PermissionService
import ai.lail.api.services.RateLimitingService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerExecutionChain
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.*

@Component
abstract class AbstractFilter : OncePerRequestFilter() {
    private val controllersPackage = "ai.lail.api.controllers"

    @Autowired
    lateinit var jwtService: JwtService

    @Autowired
    lateinit var permissionService: PermissionService

    @Autowired
    lateinit var adminService: AdminService

    @Autowired
    lateinit var rateLimitingService: RateLimitingService

    private fun getPrincipleName(): String {
        return jwtService.getPrincipalName()
    }

    /**
     * Get the user authentication information from the request
     * @param request The request being made
     *
     * @return The user authentication information or null if the user is not authenticated
     */
    protected fun getAuthentication(request: HttpServletRequest): AuthUser? {
        val cookies = request.cookies
        val authCookie = cookies?.find { it.name == getPrincipleName() }?.value.orEmpty()
        if (authCookie.isEmpty()) return null

        val user = jwtService.verifyToken(authCookie) ?: return null
        val permissions = permissionService.getUserPermissions(user.id)
        return AuthUser(user, permissions)
    }

    /**
     * Get the handler method of the route being accessed based on the request
     * @param request The request being made
     * @param context The application context
     * @return The handler method of the route being accessed or null if the route is not a controller method
     */
    protected fun getHandlerMethod(request: HttpServletRequest, context: ApplicationContext): HandlerMethod? {
        return try {
            val reqHandlerMapping =
                context.getBean("requestMappingHandlerMapping") as RequestMappingHandlerMapping

            val handlerExeChain: HandlerExecutionChain = reqHandlerMapping.getHandler(request) as HandlerExecutionChain
            var handlerMethod: HandlerMethod? = null
            if (Objects.nonNull(handlerExeChain)) {
                handlerMethod = handlerExeChain.handler as HandlerMethod
                if (!handlerMethod.beanType.name.startsWith(controllersPackage)) handlerMethod = null
            }
            handlerMethod
        } catch (e: Exception) {
            // Most likely cast exception on `handlerExeChain` (MessageRequest instead of RequestMapping?)
            null
        }
    }
}