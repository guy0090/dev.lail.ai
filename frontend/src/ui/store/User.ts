import { defineStore } from 'pinia'

import * as RestApi from '@/api/http/Api'
import {
  UserSelfDto,
  UserDetailsDto,
  UserSettingsDto,
  UserDto,
} from '@/api/common/dto/ResponseDtos'
import type { BasicRoleDto } from '@/api/common/dto/ResponseDtos'
import { UserSettingsResetDto, UserSettingsUpdateDto } from '@/api/common/dto/RequestDtos'
import type { UserDetailsChanged } from '@/api/websocket/messages/Messages'

export const userStore = defineStore('userStore', {
  state: () => {
    return {
      loggedIn: false,
      user: localStorage.getItem('user')
        ? new UserSelfDto(JSON.parse(localStorage.getItem('user')!))
        : undefined,
      discordCdn: 'https://cdn.discordapp.com/',
      token: undefined as string | undefined,
      permissions: undefined as string[] | undefined,
      roles: [] as BasicRoleDto[],
      settings: {} as UserSettingsDto,
      following: [] as UserDto[],
    }
  },

  getters: {
    isLoggedIn(state) {
      return state.loggedIn
    },

    getUser(state) {
      return state.user
    },

    getToken(state) {
      return state.token
    },

    getPermissions(state) {
      return state.permissions
    },

    getDiscordUsername(state) {
      return `${state.user?.discordUsername}#${state.user?.discriminator}`
    },

    getSettings(state) {
      return state.settings
    },

    getRoles(state) {
      return state.roles
    },

    getFollowing(state) {
      return state.following
    }
  },

  actions: {
    getUserAvatar() {
      const user = this.user as UserSelfDto
      let avatar = ''
      if (user?.avatar === null) {
        avatar = `${this.discordCdn}/embed/avatars/${parseInt(user.discriminator) % 5}.png`
      } else {
        const isGIF = user.avatar.startsWith('a_')
        avatar = `${this.discordCdn}avatars/${user.discordId}/${user.avatar}${
          isGIF ? '.gif' : '.png'
        }`
      }

      return avatar
    },

    setUser(user: UserSelfDto) {
      this.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },

    updateInfo(info: UserDetailsDto) {
      this.setUser(info.user)
      this.setSettings(info.settings)
      this.setPermissions(info.permissions)
      this.setRoles(info.roles)
      this.setToken(info.token)
      this.setLoggedIn(true)
    },

    setSettings(settings: UserSettingsDto) {
      this.settings = settings
    },

    setPermissions(permissions: string[]) {
      this.permissions = permissions
    },

    setRoles(roles: BasicRoleDto[]) {
      this.roles = roles
    },

    setToken(token: string) {
      this.token = token
    },

    setLoggedIn(loggedIn: boolean) {
      this.loggedIn = loggedIn
    },

    setFollowing(following: UserDto[]) {
      this.following = following
    },

    addFollowing(user: UserDto) {
      this.following.push(user)
    },

    removeFollowing(userId: string) {
      this.following = this.following.filter(u => u.id !== userId)
    },

    isFollowing(userId: string) {
      return this.following.find(u => u.id === userId) !== undefined
    },

    async updateSetting(update: UserSettingsUpdateDto): Promise<boolean> {
      try {
        const settings = await RestApi.updateSelfSettings(update)
        this.setSettings(settings)
        return true
      } catch (e) {
        console.error(`Failed updating settings: ${update}`, e)
        return false
      }
    },

    async resetSetting(key: string): Promise<boolean> {
      try {
        const body = new UserSettingsResetDto({ [key]: true })
        const settings = await RestApi.resetSelfSettings(body)
        this.setSettings(settings)
        return true
      } catch (err) {
        console.error(`Failed to reset setting '${key}'`, err)
        return false
      }
    },

    async logout() {
      try {
        await RestApi.logout()
      } catch (e: any) {
        console.error('Failed to logout', e)
      }
      this.reset()
      this.$router.push({ name: 'home' })
    },

    reset() {
      this.user = undefined
      this.settings = new UserSettingsDto({})
      this.permissions = []
      this.roles = []
      this.token = undefined
      this.loggedIn = false
      this.following = []
      localStorage.removeItem('user')
    },

    hasPermission(permission: string) {
      if (this.permissions === undefined || this.permissions.length === 0) return false
      else if (this.permissions.includes('*')) return true

      const nodes = permission.split('.')
      for (let i = 0; i < nodes.length; i++) {
        const node = nodes.slice(0, i + 1).join('.')
        if (this.permissions.includes(node)) return true
      }
      return false
    },

    updateUserDetails(data: any) {
      if (this.user === undefined || !this.token) return

      const update = data as UserDetailsChanged
      if (update.getPermissions()) this.setPermissions(update.getPermissions()!)
      if (update.getRoles()) this.setRoles(update.getRoles()!)
      if (update.getSettings()) this.setSettings(update.getSettings()!)
    }
  }
})
