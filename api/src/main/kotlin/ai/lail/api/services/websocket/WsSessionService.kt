package ai.lail.api.services.websocket

import ai.lail.api.data.users.User
import ai.lail.api.data.websocket.WsUserSession
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.TimeUnit

@Service
class WsSessionService {
    private val logger: Logger = LoggerFactory.getLogger(WsSessionService::class.java)
    private final val sessions: MutableList<WsUserSession> = mutableListOf()

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    private fun cleanSessions() {
        val before = sessions.size
        sessions.removeIf { !it.session.isOpen }.also {
            val diff = before - sessions.size
            if (diff > 0) logger.info("Removed $diff closed sessions")
        }
    }

    fun getSessionByUserId(id: ObjectId): List<WsUserSession> {
        return sessions.filter { it.userId == id }
    }

    fun getSessionById(id: String): WsUserSession? {
        return sessions.find { it.session.id == id }
    }

    fun saveSession(session: WebSocketSession) {
        val userSession = WsUserSession(session)
        val user = getUser(session)

        if (user != null) userSession.userId = user.id
        sessions.add(userSession)
    }

    fun isUserSession(session: WebSocketSession): Boolean {
        return session.attributes["user"] != null
    }

    fun getUser(session: WebSocketSession): User? {
        return if (isUserSession(session)) {
            session.attributes["user"] as User
        } else {
            null
        }
    }

    /**
     * Deletes session for a given socket ID
     */
    fun deleteWebSocketSession(id: String) {
        // `it` can sometimes be null when an unhandled exception after connection close happens... TODO: investigate
        val session = sessions.find { it.session.id == id }
        if (session != null) {
            session.session.close()
            sessions.remove(session)
        }
    }

    /**
     * Deletes all sessions for a user
     */
    fun revokeUserSessions(id: ObjectId) {
        val userSessions = getSessionByUserId(id)
        userSessions.forEach {
            it.session.close()
        }

        sessions.removeIf {
            it.userId == id
        }
    }

    fun getSessionIds(): List<String> {
        return sessions.map { it.session.id }
    }

    fun getSessions(): List<WsUserSession> {
        return sessions
    }
}
