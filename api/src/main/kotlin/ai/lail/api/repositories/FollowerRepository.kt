package ai.lail.api.repositories

import ai.lail.api.data.followers.Followers
import org.springframework.data.mongodb.repository.MongoRepository

interface FollowerRepository : MongoRepository<Followers, String>