package ai.lail.api.annotations

/**
 * Annotation to provide a permission for a route.
 *
 * If [value] is not set, but [inherit] is, the route will inherit any permission
 * set in the parent controller.
 *
 * If [inherit] is false as well as [value] being empty,
 * the route will not be checked for any permission.
 *
 * An example [ControllerPermission] with [value] "admin" and a [RoutePermission] with
 * [value] "users" will result in the route being checked for the permission "admin.user".
 *
 * @param value The permission to check for. Leave empty for no permission required.
 * @param inherit Whether to inherit the permission from the parent controller.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RoutePermission(
    val value: String = "",
    val inherit: Boolean = true
)
