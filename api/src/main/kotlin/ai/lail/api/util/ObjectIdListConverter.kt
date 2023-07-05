package ai.lail.api.util

import com.fasterxml.jackson.databind.util.StdConverter
import org.bson.types.ObjectId

class ObjectIdListConverter : StdConverter<List<ObjectId>, List<String>>() {
    override fun convert(value: List<ObjectId>): List<String> {
        return value.map { it.toString() }
    }
}