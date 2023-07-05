package ai.lail.api.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class UserType(val value: Int) {
    SYSTEM(0),
    USER(1);

    @JsonValue
    fun toValue() = ordinal
}