package ai.lail.api.controllers.admin

import ai.lail.api.annotations.ControllerPermission
import ai.lail.api.annotations.RoutePermission
import ai.lail.api.controllers.AdminController
import ai.lail.api.data.users.User
import ai.lail.api.services.UserService
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@ControllerPermission("users.manage")
class UsersController(val userService: UserService) : AdminController() {
    val logger: Logger = LoggerFactory.getLogger(UsersController::class.java)

    @RoutePermission("view")
    @GetMapping("$ROUTE/{userId}")
    fun getUser(@PathVariable userId: String): User? {
        return try {
            val toObjectId = ObjectId(userId)
            userService.find(toObjectId)
        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid user ID: $userId")
            null
        }
    }

    companion object {
        const val ROUTE = "/users"
    }
}