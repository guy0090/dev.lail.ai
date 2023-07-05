import type { Router } from "vue-router";
import { userStore } from "@/ui/store/User";
import * as RestApi from '@/api/http/Api'
import { utilStore } from '@/ui/store/Util'

export const configure = (router: Router) => {
  router.beforeEach(async (to) => {
    const userState = userStore()
    const user = userState.getUser

    const permission = to.meta.permission as string | undefined
    if (!permission) {
      // console.log("PermissionGuard: No permission required for route")
      return true
    } else if (!user) {
      // console.log("PermissionGuard: User not logged in")
      return { name: 'home' }
    } else {
      let canAccess = false
      if (!userState.getPermissions) {
        // console.log("PermissionGuard: Checking user permissions (asking API)")
        canAccess = await RestApi.selfHasPermission(permission)
      } else {
        // console.log("PermissionGuard: Checking user permissions (w/o asking API)")
        canAccess = userState.hasPermission(permission)
      }

      if (canAccess) {
        // console.log(`PermissionGuard: User has permission ${permission}`)
        return true
      } else {
        // console.log("PermissionGuard: User does not have permission")
        return { name: 'home' }
      }
    }
  })

  router.afterEach((to) => {
    const utilState = utilStore()
    if (to.name) utilState.setActiveNav(to.name.toString())
  })
}
