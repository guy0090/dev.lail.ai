package ai.lail.api.config.properties

import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Configuration
import java.time.Instant

@Configuration
class ProjectProperties(private val properties: BuildProperties) {

    /**
     * The name of the project.
     */
    var name: String = properties.name

    /**
     * The version of the project.
     */
    var version: String = properties.version

    /**
     * The artifact of the project.
     */
    var artifact: String = properties.artifact

    /**
     * The group of the project.
     */
    var group: String = properties.group

    /**
     * The build time of the project.
     */
    var buildTime: Instant = properties.time

}