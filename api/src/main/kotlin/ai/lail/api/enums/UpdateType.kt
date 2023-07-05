package ai.lail.api.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class UpdateType(@JsonValue val id: Int) {
    ADD(0),
    REMOVE(1),
}