package ai.lail.api.controllers.admin

import ai.lail.api.annotations.ControllerPermission
import ai.lail.api.annotations.RoutePermission
import ai.lail.api.controllers.AdminController
import ai.lail.api.services.EncounterService
import org.springframework.web.bind.annotation.*

@RestController
@ControllerPermission("encounters.manage")
class EncountersController(val encounterService: EncounterService) : AdminController() {

    @GetMapping("$ROUTE/count")
    @RoutePermission("view")
    fun getEncountersCount(): Long {
        return encounterService.count()
    }

    companion object {
        const val ROUTE = "/encounters"
    }
}