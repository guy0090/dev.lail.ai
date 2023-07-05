package ai.lail.api.util

import ai.lail.api.config.exceptions.GlobalExceptionHandler
import ai.lail.api.exceptions.common.InvalidObjectIdException
import org.bson.types.ObjectId

object ObjectIdHelper {

    /**
     * Utility function to get an ObjectId from a string while
     * throwing a custom [InvalidObjectIdException] if the string is
     * not a valid [ObjectId].
     *
     * This is done so that REST controllers can validate passed [ObjectId]
     * request parameters and throw a custom exception that can be properly
     * handled by the [GlobalExceptionHandler]
     */
    @Throws(InvalidObjectIdException::class)
    fun getId(id: String): ObjectId {
        return try {
            ObjectId(id)
        } catch (e: IllegalArgumentException) {
            throw InvalidObjectIdException("Invalid Mongo ObjectId", e.cause)
        }
    }

}