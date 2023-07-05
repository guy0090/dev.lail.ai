package ai.lail.api.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class OAuthClient(val id: String) {
    WEB("web"),
    DESKTOP("desktop");
}