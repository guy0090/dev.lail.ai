package ai.lail.api.controllers

import ai.lail.api.annotations.ControllerPermission
import ai.lail.api.annotations.RateLimit
import ai.lail.api.annotations.RoutePermission
import ai.lail.api.data.users.AuthUser
import ai.lail.api.dto.requests.users.*
import ai.lail.api.dto.responses.users.*
import ai.lail.api.enums.PrivacySetting
import ai.lail.api.exceptions.settings.users.UserSettingsNotFoundException
import ai.lail.api.exceptions.users.UserNotFoundException
import ai.lail.api.exceptions.users.UserProfilePrivateException
import ai.lail.api.permissions.Permission
import ai.lail.api.permissions.nodes.UserPermission
import ai.lail.api.security.SecurityConstants.AUTH_USER
import ai.lail.api.services.*
import ai.lail.api.util.ObjectIdHelper
import ai.lail.api.util.getAuthUser
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@ControllerPermission(UserPermission.ROOT)
@RateLimit(50)
class UserController(
    val userService: UserService,
    val userSettingsService: UserSettingsService,
    val permissionService: PermissionService,
    val followerService: FollowerService,
    val encounterSummaryService: EncounterSummaryService,
) {
    val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

    @RoutePermission(UserPermission.VIEW)
    @PostMapping("/")
    fun getUser(@Valid @RequestBody dto: UserViewDto, request: HttpServletRequest): UserDto? {
        val user = getUserFromDto(dto)
        val viewable = canView(user, request)
        if (!viewable) throw UserProfilePrivateException()

        return user
    }


    @RoutePermission(inherit = false)
    @PostMapping("/many")
    fun getUsers(@Valid @RequestBody dto: UsersViewDto, request: HttpServletRequest): List<UserDto> {
        val limit = getLimitChange(request, dto.userIds.size)
        val privacy = getPrivacyChange(request, dto.privacy)
        val userObjects = dto.userIds.map { ObjectIdHelper.getId(it) }.subList(0, limit)
        return userService.getManyUserDto(userObjects, privacy)
    }


    @RoutePermission(inherit = false)
    @PostMapping("/profile")
    fun getUserProfile(@Valid @RequestBody dto: UserViewDto, request: HttpServletRequest): UserProfileDto {
        val user = getUserFromDto(dto)
        val viewable = canView(user, request)
        if (!viewable) throw UserProfilePrivateException()

        val followers = followerService.countFollowers(user.id)
        return UserProfileDto(user, emptyList(), followers)
    }

    /// SELF ///

    @RoutePermission(UserPermission.SELF_VIEW)
    @GetMapping("/me")
    fun getSelf(@RequestAttribute(AUTH_USER) authUser: AuthUser): UserSelfDetailsDto? {
        val user = authUser.user
        val settings = userSettingsService.find(user.id) ?: throw UserSettingsNotFoundException(user.id)
        val roles = permissionService.getUserRoles(user.id)
        val permissions = permissionService.getUserPermissions(user.id)
        val uploads = encounterSummaryService.countByOwner(user.id)

        return UserSelfDetailsDto(user, settings, permissions, roles, uploads)
    }


    @RoutePermission(UserPermission.SELF_SETTINGS_VIEW)
    @GetMapping("/me/settings")
    fun getSelfSettings(@RequestAttribute(AUTH_USER) authUser: AuthUser): UserSettingsDto {
        val user = authUser.user
        val settings = userSettingsService.find(user.id) ?: throw UserNotFoundException(user.id)
        return UserSettingsDto(settings)
    }


    @RoutePermission(UserPermission.SELF_SETTINGS_EDIT)
    @PatchMapping("/me/settings")
    fun updateSelfSettings(
        @Valid @RequestBody update: UserSettingsUpdateDto,
        @RequestAttribute(AUTH_USER) authUser: AuthUser
    ): UserSettingsDto {
        val user = authUser.user
        var settings = userSettingsService.find(user.id) ?: throw UserNotFoundException(user.id)
        settings = userSettingsService.update(settings.id, update)
        return UserSettingsDto(settings)
    }


    @RoutePermission(UserPermission.SELF_SETTINGS_EDIT)
    @PutMapping("/me/settings")
    fun resetSelfSettings(
        @Valid @RequestBody update: UserSettingResetDto,
        @RequestAttribute(AUTH_USER) authUser: AuthUser
    ): UserSettingsDto {
        val user = authUser.user
        var settings = userSettingsService.find(user.id) ?: throw UserNotFoundException(user.id)
        settings = userSettingsService.reset(settings.id, update)
        return UserSettingsDto(settings)
    }

    @RoutePermission(UserPermission.SELF_VIEW)
    @GetMapping("/me/permissions")
    fun getSelfPermissions(@RequestAttribute(AUTH_USER) authUser: AuthUser): Set<String> {
        val user = authUser.user
        return permissionService.getUserPermissions(user.id)
    }

    @RoutePermission(UserPermission.SELF_VIEW)
    @PostMapping("/me/permissions")
    fun selfHasPermission(
        @Valid @RequestBody permission: String,
        response: HttpServletResponse,
        @RequestAttribute(AUTH_USER) authUser: AuthUser
    ) {
        val permissions = authUser.permissions
        val isValidPermission = Permission.isValidPermission(permission)
        if (!isValidPermission || !permissionService.hasPermission(permission, permissions)) {
            response.status = HttpServletResponse.SC_FORBIDDEN
        }
    }

    /// Misc. helpers

    private fun getUserFromDto(dto: UserViewDto): UserDto {
        return dto.slug.let {
            if (it == null) {
                val targetId = ObjectIdHelper.getId(dto.id!!)
                userService.getUserDto(targetId)
            } else {
                userService.getUserDtoFromSlug(it)
            }
        } ?: throw UserNotFoundException()
    }

    private fun canView(user: UserDto, request: HttpServletRequest): Boolean {
        val defaultValue = false
        val requester = request.getAuthUser()

        val isOwnProfile = requester != null && requester.user.id.toHexString() == user.id
        if (isOwnProfile) return true

        val canViewPrivate = permissionService.permittedChange(UserPermission.MANAGE_VIEW, request, true, defaultValue)
        return !(user.isPrivate() && !canViewPrivate)
    }

    private fun getPrivacyChange(request: HttpServletRequest, privacy: List<PrivacySetting>): List<PrivacySetting> {
        val defaultPrivacySetting = listOf(PrivacySetting.PUBLIC, PrivacySetting.UNLISTED)
        return permissionService.permittedChange(
            UserPermission.MANAGE_VIEW, request, privacy, defaultPrivacySetting
        )
    }

    private fun getLimitChange(request: HttpServletRequest, limit: Int?): Int {
        val defaultLimit = 20
        return permissionService.permittedChange(
            UserPermission.MANAGE_VIEW, request, limit, defaultLimit
        )
    }
}