package ai.lail.api.config

import ai.lail.api.config.services.SystemConfigurationService
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class CorsConfiguration(val systemConfigurationService: SystemConfigurationService) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        val frontend = systemConfigurationService.getFrontendUrl()
        registry.addMapping("/**")
            .allowedOrigins(frontend)
            .allowedMethods("*")
            .allowCredentials(true)
    }
}