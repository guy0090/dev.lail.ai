package ai.lail.api.security.interceptors

import ai.lail.api.data.users.User
import ai.lail.api.enums.ServiceStatus
import ai.lail.api.permissions.nodes.AdminPermission
import ai.lail.api.services.*
import ai.lail.api.util.CryptoUtil
import ai.lail.api.util.ObjectIdHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.web.util.UriComponentsBuilder

class WsAuthenticationInterceptor(
    private val jwtService: JwtService,
    private val userService: UserService,
    private val vaultService: VaultService,
    private val adminService: AdminService,
    private val permissionService: PermissionService
) : HandshakeInterceptor {
    val logger: Logger = LoggerFactory.getLogger(WsAuthenticationInterceptor::class.java)

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        attributes.remove("user") // Shouldn't be possible, but just in case

        val user = getAuthUser(request)
        if (user != null) attributes["user"] = user

        var allowedToConnect = false
        when (adminService.getSystemStatus()) {
            ServiceStatus.MAINTENANCE -> allowedToConnect = canBypassMaintenance(user)
            ServiceStatus.READY -> allowedToConnect = true
            else -> { /* FOOBAR */ }
        }

        return allowedToConnect
    }

    override fun afterHandshake(
        request: ServerHttpRequest, response: ServerHttpResponse, wsHandler: WebSocketHandler, exception: Exception?
    ) { // NOOP
    }

    private fun canBypassMaintenance(user: User?): Boolean {
        if (user == null) return false
        return permissionService.hasPermission(user.id, AdminPermission.MAINTENANCE)
    }

    private fun getAuthUser(request: ServerHttpRequest): User? {
        val uri = request.uri.toString()
        val queryParams = UriComponentsBuilder.fromUriString(uri).build().queryParams;
        val principal = queryParams.getFirst("token") ?: return null
        if (principal.isEmpty()) return null

        val token = jwtService.decodeToken(principal) ?: return null
        val payload = jwtService.getPayload(token)
        val user = userService.find(ObjectIdHelper.getId(payload.id)) ?: return null
        val salt = vaultService.getUserSalt(user.id) ?: return null

        return if (CryptoUtil.isHashValid(user, salt, payload.hash)) user else null
    }

}