package ai.lail.api.repositories

import ai.lail.api.data.permissions.Role
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RoleRepository : MongoRepository<Role, String> {
    fun findRoleByName(name: String): List<Role>?

    @Aggregation(pipeline = [
        "{\$match: { _id: {\$in: ?0}}}",
        "{\$count: \"count\"}"
    ])
    fun countByIds(ids: Collection<ObjectId>): Optional<Int>
}