package ai.lail.api.data.notifications

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId

abstract class Notification {
    abstract var type: Int
    var timestamp: Long = System.currentTimeMillis()

    @JsonSerialize(using = ToStringSerializer::class)
    var id: ObjectId = ObjectId()
    var seen: Boolean = false
    abstract var content: Map<String, Any>
    protected abstract fun initContent(): Map<String, Any>
}