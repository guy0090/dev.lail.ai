package ai.lail.migrations.v1

import ai.lail.api.permissions.nodes.WebSocketPermission
import ai.lail.api.services.RoleService
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution

@ChangeUnit(id = "add-default-web-socket-permissions-to-default-member-role", order = "1", author = "lailai")
class AddDefaultWeSocketPermissionsToDefaultMemberRole() {

    @Execution
    fun execution(roleService: RoleService) {
        val role = roleService.getDefaultRoles().find { it.name == "Member" }
        if (role != null) {
            roleService.addPermissions(role, WebSocketPermission.defaults.toSet())
        }
    }

    @RollbackExecution
    fun rollback(roleService: RoleService) {
        val role = roleService.getDefaultRoles().find { it.name == "Member" }
        if (role != null) {
            roleService.removePermissions(role, WebSocketPermission.defaults.toSet())
        }
    }
}