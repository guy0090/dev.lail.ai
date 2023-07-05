package ai.lail.api.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

/**
 * Only used to configure CSRF, CORS and Session Management. All other security is handled by the filters found
 * in [ai.lail.api.security.filters].
 */
@Configuration
@EnableWebSecurity
class SecurityFilterConfig {

    @Bean
    fun securityConfig(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher("/**").csrf().disable().cors().and().sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeHttpRequests()
            .anyRequest().permitAll()

        return http.build()
    }
}