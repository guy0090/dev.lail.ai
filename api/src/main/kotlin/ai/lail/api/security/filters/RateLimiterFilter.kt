package ai.lail.api.security.filters

import ai.lail.api.annotations.RateLimit
import io.github.bucket4j.ConsumptionProbe
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import java.lang.reflect.Method
import java.util.*

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 4)
class RateLimiterFilter : AbstractFilter() {
    @Autowired
    lateinit var appContext: ApplicationContext

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.debug("RateLimiterFilter.doFilterInternal() - Request: ${request.method} ${request.requestURI}")
        if (request.method == "OPTIONS") { // CORS preflight
            filterChain.doFilter(request, response)
            return
        }

        try {
            val handler = getHandlerMethod(request, appContext)
            if (handler == null && !request.requestURI.startsWith("/events")) {
                logger.warn("RateLimiterFilter.doFilterInternal() - Handler is null >> ${request.requestURI}") // If a non-mapped route is accessed, the handler will be null
                response.sendError(500, "Internal server error")
                return
            }

            val rateLimit = handler?.let { getRateLimit(it) }
            if (rateLimit == null) {
                filterChain.doFilter(request, response)
                return
            }

            val tokenBucket = rateLimitingService.resolveBucket(request, rateLimit)

            val probe: ConsumptionProbe = tokenBucket.tryConsumeAndReturnRemaining(1)
            if (probe.isConsumed) {
                response.addHeader("X-Rate-Limit-Remaining", "${probe.remainingTokens}")
                filterChain.doFilter(request, response)
            } else {
                response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded, please try again in bit")
            }

        } catch (e: Exception) {
            logger.error("RateLimiterFilter.doFilterInternal() - Exception: ${e.message}")
            response.sendError(500, "Internal server error")
            return
        }
    }

    /**
     * Get the rate limit annotation of the route located on the controller method
     * @param handler The handler method of the route being accessed
     * @return The rate limit annotation of the route
     */
    private fun getRouteRateLimitAnnotation(handler: Any): RateLimit? {
        val handlerMethod: HandlerMethod = handler as HandlerMethod
        val method: Method = handlerMethod.method
        return method.getAnnotation(RateLimit::class.java)
    }

    /**
     * Get the rate limit annotation of the route located on the controller class
     * @param handler The handler method of the route being accessed
     * @return The rate limit annotation of the controller class
     */
    private fun getControllerRateLimitAnnotation(handler: Any): RateLimit? {
        val handlerMethod: HandlerMethod = handler as HandlerMethod
        val method: Method = handlerMethod.method
        return method.declaringClass.getAnnotation(RateLimit::class.java)
    }

    /**
     * Get the rate limit of the route being accessed based on the rate limit annotations.
     *
     * - If the route has a [RateLimit] annotation, the value of the annotation will be returned.
     * - If the route has no [RateLimit] annotation, the value of the controller rate limit annotation will be returned.
     * - If no rate limit is specified on the controller class and controller methods, then null will be returned.
     *
     * @param handler The handler method of the route being accessed
     * @return The rate limit of the route being accessed or null if none found (no rate limit required)
     */
    private fun getRateLimit(handler: HandlerMethod): RateLimit? {
        return getRouteRateLimitAnnotation(handler) ?: getControllerRateLimitAnnotation(handler)
    }
}