package ai.lail.api.controllers.admin

import ai.lail.api.annotations.ControllerPermission
import ai.lail.api.annotations.RoutePermission
import ai.lail.api.controllers.AdminController
import ai.lail.api.data.users.AuthUser
import ai.lail.api.dto.responses.roles.RoleDto
import ai.lail.api.security.SecurityConstants.AUTH_USER
import ai.lail.api.services.*
import ai.lail.api.services.websocket.WsPublishService
import ai.lail.api.websocket.events.UserDetailsChangedEvent
import ai.lail.api.websocket.events.WelcomeEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@ControllerPermission("admin")
class SystemController(
    val adminService: AdminService,
    val permissionService: PermissionService,
    val roleService: RoleService,
    val wsPublishService: WsPublishService,
    val vaultService: VaultService
) :
    AdminController() {

    val logger: Logger = LoggerFactory.getLogger(SystemController::class.java)

    @RoutePermission(inherit = false)
    @PostMapping("$ROUTE/init")
    fun initService(@RequestBody key: String, @RequestAttribute(AUTH_USER) authUser: AuthUser?): String {
        val settings = adminService.getSettings()
        if (adminService.isInitialized()) throw IllegalStateException("Service already initialized")
        val requester = authUser?.user ?: throw IllegalStateException("User not logged in")
        val initKey = vaultService.getInitKey() ?: throw IllegalStateException("No init key set")

        logger.info("User ${requester.id} is trying to initialize the service")
        if (key == initKey) {
            permissionService.addRole(requester.id, roleService.getSuperuserRole().id)
            val copy = settings.copy()

            copy.initDone = true
            // copy.uploadsEnabled = true
            adminService.saveSettings(copy)
            vaultService.deleteInitKey()

            try {
                wsPublishService.publishToAll(WelcomeEvent(null, null, true))

                val roles = permissionService.getUserRoles(requester.id).map { RoleDto(it) }
                val permissions = permissionService.getUserPermissions(requester.id)
                val changeEvent = UserDetailsChangedEvent(permissions, roles)
                wsPublishService.publishToUser(requester.id, changeEvent)
            } catch (e: Exception) {
                logger.error("Failed to publish backend ready event", e)
            }

            logger.info("Service initialized by user ${requester.id}")
            return "Service initialized"
        }

        throw IllegalStateException("Invalid key")
    }

    companion object {
        const val ROUTE = "/system"
    }
}