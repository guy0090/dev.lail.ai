package ai.lail.api

import io.mongock.runner.springboot.EnableMongock
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableMongock
@EnableConfigurationProperties
@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class]) // minor inconvenience, meet sledgehammer
@EnableScheduling
class LostArkLogsApiApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<LostArkLogsApiApplication>(*args) {
                setBannerMode(Banner.Mode.CONSOLE)
            }
        }
    }

}