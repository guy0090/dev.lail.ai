package ai.lail.api.annotations

/**
 * Annotation to provide a root permission for a controller.
 * All methods in the controller will be checked for the permission unless
 * the method is annotated with [RoutePermission] and the permission is not
 * set to not be inherited (default = true).
 *
 * @param value The permission to check for.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ControllerPermission(
    val value: String = "",
)