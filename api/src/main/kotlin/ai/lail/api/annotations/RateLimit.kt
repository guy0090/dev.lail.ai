package ai.lail.api.annotations

import java.time.temporal.ChronoUnit

/**
 * Annotation to provide a rate limit for a route.
 * All methods in the controller will be checked for a rate limit.
 * If a certain route has no rate limit, then it will use the rate limit
 * that is set on the controller class if it exists.
 *
 * @param requests The number of requests a user can make on a route.
 * @param time The duration it takes to replenish the number of requests a user can make.
 * @param timeUnit The unit of time used which is set to seconds.
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class RateLimit(
    val requests: Long = 100,
    val time: Long = 60,
    val timeUnit: ChronoUnit = ChronoUnit.SECONDS,
)
