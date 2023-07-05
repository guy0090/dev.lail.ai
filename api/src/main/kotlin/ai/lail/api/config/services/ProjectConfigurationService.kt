package ai.lail.api.config.services

import ai.lail.api.config.properties.ProjectProperties
import org.springframework.stereotype.Service

@Service
class ProjectConfigurationService(val props: ProjectProperties) {

    fun getName() = props.name
    fun getVersion() = props.version
    fun getArtifact() = props.artifact
    fun getGroup() = props.group
    fun getBuildTime() = props.buildTime
}