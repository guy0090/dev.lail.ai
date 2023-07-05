package ai.lail.api.websocket

object WsConstants {

    /**
     * The maximum number of pending responses.
     */
    const val MAX_PENDING = 10000

    /**
     * The time to live for pending responses, in milliseconds.
     */
    const val PENDING_TTL_MS = 30000 // 30 seconds

}