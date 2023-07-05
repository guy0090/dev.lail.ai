package ai.lail.api.data.encounters

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document("encounters")
data class Encounter(
    @Id
    var id: ObjectId = ObjectId(),
    @Field("data")
    var data: LinkedHashMap<String, Any> = LinkedHashMap(),
)