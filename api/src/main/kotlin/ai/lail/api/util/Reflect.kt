package ai.lail.api.util

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

/**
 * Reflect on your sins
 */
object Reflect {

    /**
     * Returns the values of all const properties of a class. Assumes all const values are of type String.
     * @param clazz The class to get the const values from.
     */
    fun getAllConstValues(clazz: KClass<out Any>): Set<String> {
        val properties = clazz.memberProperties
        val values = mutableSetOf<String>()
        properties.forEach { prop ->
            if (prop.isConst) {
                val content = prop.getter.call()
                if (content is String) values.add(content)
            }
        }
        return values
    }
}