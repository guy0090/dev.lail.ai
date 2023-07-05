package ai.lail.api.services

import ai.lail.api.annotations.RateLimit
import ai.lail.api.util.getRateLimitKey
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap

@Service
class RateLimitingService {
    private final val buckets = ConcurrentHashMap<String, Bucket>()

    fun resolveBucket(request: HttpServletRequest, rateLimit: RateLimit): Bucket =
        buckets.computeIfAbsent(request.getRateLimitKey()) {
            createBucket(rateLimit)
        }

    fun createBucket(rateLimit: RateLimit): Bucket {
        return Bucket.builder().addLimit(
            Bandwidth.classic(
                rateLimit.requests,
                Refill.intervally(
                    rateLimit.requests,
                    Duration.of(rateLimit.time, rateLimit.timeUnit)
                )
            )
        ).build()
    }
}