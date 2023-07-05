package ai.lail.api.websocket.commands

import ai.lail.api.exceptions.AbstractException
import ai.lail.api.exceptions.common.MissingAuthPrincipalException
import ai.lail.api.exceptions.websocket.InvalidWsContentException
import ai.lail.api.services.FollowerService
import ai.lail.api.services.NotificationService
import ai.lail.api.services.UserService
import ai.lail.api.websocket.CommandContent
import ai.lail.api.websocket.WebSocketCommand
import ai.lail.api.websocket.WebSocketEvent
import ai.lail.api.websocket.WebSocketResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.springframework.context.ApplicationContext

class GetFollowersCommand(body: CommandContent) : WebSocketCommand(body) {
    override val event: WebSocketEvent = WebSocketEvent.GetFollowers
    val user by lazy { sender ?: throw MissingAuthPrincipalException() }
    private val targetId = getObjectId("targetId")

    override fun handle(response: WebSocketResponse, context: ApplicationContext) {
        val content = ObjectMapper().createObjectNode()
        try {
            val followerService = context.getBean(FollowerService::class.java)
            val followers = followerService.countFollowers(targetId)

            content.put("message", "Got count of followers for user $targetId")
            content.putPOJO("result", followers)
            response.complete(content)
        } catch (e: Exception) {
            if (e is AbstractException) content.put("message", e.message)
            else content.put("message", "An unknown error occurred")
            response.fail(content)
        }
    }
}