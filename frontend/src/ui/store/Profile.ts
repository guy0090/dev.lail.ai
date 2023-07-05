import { Profile, UserSelfProfileDto } from "@/api/common/dto/ResponseDtos";
import { defineStore } from "pinia";
import { userStore } from './User'

export const profileStore = defineStore('profileStore', {
  state: () => {
    return {
      activeProfile: undefined as Profile | undefined,
      profiles: {} as { [key: string]: Profile },
    }
  },

  getters: {

    getActiveProfile(state): Profile | undefined {
      return state.activeProfile
    },

    getProfiles(state) {
      return state.profiles
    },

  },

  actions: {

    getLocalUserProfile(): UserSelfProfileDto | undefined {
      const userState = userStore()
      const user = userState.getUser
      if (user) return UserSelfProfileDto.fromUserState(user, userState.getRoles, userState.getSettings)
      else return undefined
    },

    setActiveProfile(profile: Profile) {
      this.addProfile(profile)
      this.activeProfile = profile
    },

    updateProfile(profile: Profile) {
      this.profiles[profile.user.id] = profile
    },

    addProfile(profile: Profile) {
      this.profiles[profile.user.id] = profile
    },

    getProfile(id: string): Profile | undefined {
      return this.profiles[id]
    },
  }
})