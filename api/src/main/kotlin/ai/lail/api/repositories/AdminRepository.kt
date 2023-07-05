package ai.lail.api.repositories

import ai.lail.api.data.settings.system.Settings
import org.springframework.data.mongodb.repository.MongoRepository

interface AdminRepository : MongoRepository<Settings, String>