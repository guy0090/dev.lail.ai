package ai.lail.api.controllers

import ai.lail.api.config.services.SystemConfigurationService
import ai.lail.api.data.discord.DiscordOAuth
import ai.lail.api.data.users.LoginUser
import ai.lail.api.data.users.AuthUser
import ai.lail.api.data.users.User
import ai.lail.api.dto.responses.users.LoginUserDto
import ai.lail.api.enums.OAuthClient
import ai.lail.api.exceptions.common.SignUpDisabledException
import ai.lail.api.exceptions.discord.DiscordGetUserException
import ai.lail.api.exceptions.discord.DiscordRefreshGrantException
import ai.lail.api.exceptions.users.UserNotFoundException
import ai.lail.api.security.SecurityConstants.AUTH_USER
import ai.lail.api.services.*
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.bson.types.ObjectId
import org.hibernate.validator.constraints.Length
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/oauth")
class LoginController(
    val userService: UserService,
    val userSettingsService: UserSettingsService,
    val discordApiService: DiscordApiService,
    val discordOAuthService: DiscordOAuthService,
    val permissionService: PermissionService,
    val jwtService: JwtService,
    val vaultService: VaultService,
    val systemConfigurationService: SystemConfigurationService,
    val encounterSummaryService: EncounterSummaryService,
    val adminService: AdminService
) {
    val logger: Logger = LoggerFactory.getLogger(LoginController::class.java)

    /**
     * Endpoint responsible for registering a new user or, if the user already exists,
     * logging an existing user in (while updating their information).
     *
     * If the user is new, their information will be pulled from Discord's API and
     * all default information will be set.
     *
     * If the user is not new, their information will be updated from Discord's API
     * and their last Discord update time will be set. Discord information is only
     * updated after a set duration defined in [DiscordApiService.userRefreshTimeout].
     *
     * Regardless of whether the user is new or not, their [User] object and
     * a token for future authenticated WebSocket requests will be returned.
     */
    @PostMapping("/{code}")
    fun register(
        @Length(min = 20, max = 40)
        @PathVariable code: String,
        response: HttpServletResponse,
        request: HttpServletRequest
    ): LoginUserDto {
        val clientType: OAuthClient = request.getHeader("x-client").let {
            if (it == null) OAuthClient.WEB else OAuthClient.valueOf(it.uppercase())
        }

        val grant = discordApiService.getToken(code, clientType)
        val discordUser = discordApiService.getUser(grant!!.accessToken) // will have thrown exception if null
            ?: throw DiscordGetUserException("Could not get user from Discord (null)")

        var user: User? = userService.findByDiscordId(discordUser.id)
        if (user == null) {
            val settings = adminService.getSettings()
            if (settings.signUpAllowed.not()) throw SignUpDisabledException()
            // Create new user
            user = adminService.createNewUser(discordUser)
        } else {
            // Update existing user
            user = updateExistingUser(user)
            vaultService.getUserSecrets(user.id) // trigger cache
        }

        val userSettings = userSettingsService.find(user.id)
            ?: throw UserNotFoundException("Could not find user settings")
        discordOAuthService.save(user.id, grant)

        val authCookie = createAuthCookie(user.id)
        response.addCookie(authCookie)

        // Add extra information to the user object for initial login
        val roles = permissionService.getUserRoles(user.id)
        val permissions = permissionService.getUserPermissions(user.id)
        val uploads = encounterSummaryService.countByOwner(user.id)
        return LoginUserDto(user, userSettings, roles, permissions, authCookie.value, uploads)
    }

    /**
     * Endpoint responsible for logging the user in.
     *
     * Attempts to update any user information from Discord, then creates a new auth cookie for the user
     * and returns it in the response.
     *
     * @see updateExistingUser
     * @see createAuthCookie
     * @see LoginUser
     */
    @GetMapping("/login")
    fun login(@RequestAttribute(AUTH_USER) authUser: AuthUser, response: HttpServletResponse): LoginUserDto {
        val requester = authUser.user
        val updatedUser: User = updateExistingUser(requester)
        val authCookie = createAuthCookie(requester.id)
        response.addCookie(authCookie)

        vaultService.getUserSecrets(requester.id) // cache
        val settings = userSettingsService.find(requester.id)
            ?: throw UserNotFoundException("Could not find settings for user ${requester.id}")
        val roles = permissionService.getUserRoles(requester.id)
        val permissions = permissionService.getUserPermissions(requester.id)
        val uploads = encounterSummaryService.countByOwner(requester.id)
        return LoginUserDto(updatedUser, settings, roles, permissions, authCookie.value, uploads)
    }

    /**
     * Endpoint responsible for logging the user out in the UI.
     *
     * Very basic, only removes the auth cookie to invalidate their session locally.
     */
    @GetMapping("/logout")
    fun revoke(response: HttpServletResponse): String {
        val cookie = Cookie(jwtService.getPrincipalName(), "")
        cookie.domain = systemConfigurationService.getHost()
        cookie.path = "/"
        cookie.isHttpOnly = true
        cookie.secure = false
        cookie.maxAge = 0
        response.addCookie(cookie)

        return "Revoked"
    }

    /**
     * Creates a new auth cookie for the user.
     * @see JwtService.generateToken
     */
    private fun createAuthCookie(userId: ObjectId): Cookie {
        val token = jwtService.generateToken(userId)
        val authCookie = Cookie(jwtService.getPrincipalName(), token)
        authCookie.domain = systemConfigurationService.getHost()
        authCookie.path = "/"
        authCookie.isHttpOnly = true
        authCookie.secure = false
        authCookie.maxAge = jwtService.getExpirationSeconds()
        return authCookie
    }

    /**
     * Refreshes the user's Discord OAuth grant if it has expired.
     *
     * Will throw [DiscordRefreshGrantException] if the grant cannot be refreshed.
     */
    @Throws(DiscordRefreshGrantException::class)
    private fun refreshDiscordOAuth(user: User): DiscordOAuth {
        var grant = discordOAuthService.findById(user.id)
            ?: throw DiscordRefreshGrantException("Existing Discord OAuth not found")
        if (grant.expiresAt <= System.currentTimeMillis()) {
            logger.info("LoginController.refreshDiscordOAuth() - Refreshing Discord OAuth token for ${user.id} (${user.getFullDiscordUsername()})")
            grant = discordOAuthService.refresh(grant)
                ?: throw DiscordRefreshGrantException("Could not refresh Discord OAuth grant")
            logger.info("LoginController.refreshDiscordOAuth() - Refreshed Discord OAuth token for ${user.id} (${user.getFullDiscordUsername()})")
        }

        return grant
    }

    /**
     * Updates the user's information from Discord if it has been more than
     * [DiscordApiService.userRefreshTimeout] (milliseconds).
     *
     * Will throw [DiscordGetUserException] if the user cannot be retrieved from Discord.
     * Will throw [DiscordRefreshGrantException] if the user's OAuth grant cannot be refreshed.
     */
    @Throws(DiscordRefreshGrantException::class, DiscordGetUserException::class)
    private fun updateExistingUser(user: User): User {
        val grant = refreshDiscordOAuth(user)
        val msSinceUpdate = Instant.now().minusMillis(user.lastDiscordUpdate).toEpochMilli()

        if (msSinceUpdate >= discordApiService.userRefreshTimeout) {
            logger.info("LoginController.updateExistingUser() - Updating user ${user.id} (${user.getFullDiscordUsername()})")
            val discordUser = grant.let { discordApiService.getUser(it.access) }
                ?: throw DiscordGetUserException("Could not get Discord user from OAuth grant")

            val update = userService.updateFromDiscord(user.id, discordUser)
            logger.info("LoginController.updateExistingUser() - Updated user ${user.id} (${user.getFullDiscordUsername()})")
            return update
        }

        return user
    }
}