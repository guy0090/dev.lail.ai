package ai.lail.api.enums

import com.fasterxml.jackson.annotation.JsonValue

/**
 * The stage of startup the server is in
 * @param stage The stage of startup the server is in
 */
enum class ServiceStatus(private val stage: Int) {

    /**
     * Init runner hasn't been called yet
     */
    UNKNOWN(-1),

    /**
     * The server is starting up
     */
    STARTING(0),

    /**
     * The server is loading the default values, only used for the first time the server is started
     */
    LOADING_DEFAULTS(1),

    /**
     * The server is done loading and ready to accept connections
     */
    READY(2),

    /**
     * The server is stopping
     */
    STOPPING(3),

    /**
     * The server is in maintenance mode
     */
    MAINTENANCE(4);

    fun value(): String {
        return "${this.stage}"
    }

    @JsonValue
    fun toValue() = ordinal

    companion object {
        /**
         * Get a ServiceStatus from the name of the enum
         * @param name The name of the enum
         */
        fun fromString(name: String): ServiceStatus {
            return ServiceStatus.valueOf(name)
        }

        /**
         * Get a ServiceStatus from the value of the enum
         * @param value The value of the enum
         */
        fun fromInt(value: Int): ServiceStatus {
            return ServiceStatus.values().find { it.stage == value } ?: STARTING
        }
    }
}