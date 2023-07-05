<template>
  <template v-if="loading">
    <profile-loader :profile-id="($route.params.id as string)" />
  </template>
  <template v-else-if="error">
    <profile-error :error="error" />
  </template>
  <template v-else>
    <profile-header />
    <profile-nav />
    <router-view />
  </template>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { onBeforeRouteUpdate, useRoute } from 'vue-router';
import { userStore } from '@/ui/store/User';
import { profileStore } from '@/ui/store/Profile';

import ProfileLoader from '@/ui/components/profiles/common/ProfileLoader.vue';
import ProfileError from '@/ui/components/profiles/common/ProfileError.vue';
import ProfileHeader from '@/ui/components/profiles/common/ProfileHeader.vue';
import ProfileNav from '@/ui/components/profiles/common/ProfileNav.vue';
import { getUserProfile } from '@/api/http/Api';
import type { Profile } from '@/api/common/dto/ResponseDtos';
import { ApiException } from '@/api/http/ApiException';
import { AppException, AppExceptionCode } from '@/common/AppException';

const route = useRoute();
const userState = userStore();
const profileState = profileStore();

const error = ref(undefined as undefined | string)
const loading = ref(true)

const isOwnProfile = (id: string) => /^@?me$/i.test(id)
// ObjectID (24 hex chars) or custom user slug (2-20 chars, alphanumeric or underscore)
const isValidProfile = (id: string) => /^[a-zA-Z0-9]{24}$|^[a-zA-Z0-9_]{2,20}$/.test(id)

const tryGetCachedProfile = async (id: string): Promise<Profile> => {
  let profile = profileState.getProfile(id)
  if (profile) return profile

  profile = await getUserProfile(id)
  return profile
}

const getError = (err: any) => {
  if (err instanceof ApiException) {
    return `apiErrors.${err.code}`
  } else if (err instanceof AppException) {
    return `errors.${err.code}`
  } else {
    return "errors.0"
  }
}

onBeforeRouteUpdate(async (to, from) => {
  error.value = undefined
  let toId = to.params.id as string
  const fromId = from.params.id as string
  if (toId === fromId) return
  if (userState.getUser && toId === userState.getUser.id) toId = "me"

  // Vue router will keep the old ID in the route params, so we need to update it
  // TODO: why is it doing this? Tested setting :key on router-view with no change
  // to this behavior
  route.params.id = toId

  loading.value = true
  let updateProfile: Profile | undefined = undefined
  try {
    if (isOwnProfile(toId) && userState.isLoggedIn) {
      updateProfile = profileState.getLocalUserProfile()!
    } else if (isValidProfile(toId)) {
      updateProfile = await tryGetCachedProfile(toId)
    } else {
      throw new AppException(AppExceptionCode.InvalidUserId)
    }
    profileState.setActiveProfile(updateProfile)
  } catch (e) {
    console.error("Failed to get profile", e)
    error.value = getError(e)
  }
  loading.value = false
})

onMounted(() => {
  error.value = undefined
  let profileId = route.params.id as string;
  if (userState.getUser && userState.getUser.id === profileId) {
    profileId = "me"
    route.params.id = profileId
  }

  if (isOwnProfile(profileId) && userState.getUser) {
    if (userState.isLoggedIn === false) {
      // User directly navigated to their profile, wait for login to finish
      userState.$onAction(({ name, args }) => {
        if (name === "setLoggedIn" && args[0] === true) {
          const profile = profileState.getLocalUserProfile()!;
          profileState.setActiveProfile(profile)
        }
      })
    } else {
      const profile = profileState.getLocalUserProfile()!;
      profileState.setActiveProfile(profile)
    }

    // Refresh profile when settings are updated
    userState.$onAction(({ name, args }) => {
      if (name === "setSettings") {
        const profile = profileState.getLocalUserProfile()!;
        profile.settings = args[0];
        profileState.setActiveProfile(profile)
      }
    })
  } else if (isValidProfile(profileId)) {
    console.log("Valid user profile ID", profileId, route.meta)
    getUserProfile(profileId).then((profile) => {
      profileState.setActiveProfile(profile)
    }).catch((err) => {
      console.error("Failed to load user", err)
      error.value = getError(err)
    })
  } else {
    console.error("Invalid user profile ID", profileId)
    error.value = getError(new AppException(AppExceptionCode.InvalidUserId))
  }
  loading.value = false
})
</script>