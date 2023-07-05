package ai.lail.api.connectors

import ai.lail.api.config.properties.MongoProperties
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class MongoDbConfiguration(val env: ConfigurableEnvironment, val properties: MongoProperties) {

    private fun getDatabaseName(): String {
        val profile = env.activeProfiles[0]
        return if (profile == null || profile != "prod") "test"
        else "prod"
    }

    @Bean
    fun mongo(): MongoClient {
        val connectionString = ConnectionString("mongodb://${properties.host}:${properties.port}")
        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .applicationName("lost-ark-logs")
            .retryWrites(true)
            .build()

        return MongoClients.create(mongoClientSettings)
    }

    @Bean
    fun mongoTemplate(): MongoTemplate = MongoTemplate(mongo(), getDatabaseName())

}