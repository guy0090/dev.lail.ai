<template>
  <v-row justify="center">
    <!-- The logged in user's profile -->
    <v-col cols="12" lg="11" v-if="activeProfile instanceof UserSelfProfileDto">
      <v-card class="rounded-t rounded-0" border height="129px">
        <v-card-text class="pa-0">
          <v-row>
            <v-col cols="auto" class="pe-1">
              <v-img cover width="128" height="128" class="border-e" :src="getAvatar(activeProfile.user)"></v-img>
            </v-col>
            <v-col cols="5" sm="5" class="ps-5" align-self="center">
              <v-row class="mb-1 mt-1">
                <span class="text-h5 d-inline-block text-truncate">{{ getUsername(activeProfile) }}</span>
              </v-row>
              <v-row class="mt-0" v-if="width > thresholds.sm">
                <user-role v-for="(role, i) in activeProfile.roles" :key="i" :role="role" :show-hidden="true" />
              </v-row>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-col>
    <!-- Remote Profile (not the logged in user) -->
    <v-col cols="12" lg="11" v-else-if="activeProfile instanceof UserProfileDto">
      <v-card class="rounded-t rounded-0" border height="129px">
        <v-card-text class="pa-0">
          <v-row>
            <v-col cols="auto" class="pe-1">
              <v-img cover width="128" class="border-e" :src="getAvatar(activeProfile.user)"></v-img>
            </v-col>
            <v-col cols="6" sm="5" class="ps-5" align-self="center">
              <v-row class="mb-1 mt-1">
                <span class="text-h5 d-inline-block text-truncate">{{ activeProfile.user.username }}</span>
                <v-tooltip :text="isFollowing ? 'Unfollow' : 'Follow'" v-if="userState.getUser && userState.getToken">
                  <template v-slot:activator="{ props }">
                    <v-btn v-bind="props" color="blue" class="ms-1 mt-1" size="small" :disabled="disableFollow"
                      @click="isFollowing ? unfollowUser(activeProfile?.user.id) : followUser(activeProfile?.user)"
                      @mouseenter="hoveringFollow = true" @mouseleave="hoveringFollow = false">
                      <template v-if="isFollowing">
                        <v-icon v-if="!hoveringFollow">mdi-account-check</v-icon>
                        <v-icon v-else>mdi-account-cancel</v-icon>
                      </template>
                      <template v-else>
                        <v-icon v-if="!hoveringFollow">mdi-account-star</v-icon>
                        <v-icon v-else>mdi-account-check</v-icon>
                      </template>
                    </v-btn>
                  </template>
                </v-tooltip>
              </v-row>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { useDisplay } from 'vuetify';
import { computed, ref } from 'vue';
import UserRole from './UserRole.vue';

import { userStore } from '@/ui/store/User';
import { profileStore } from '@/ui/store/Profile';
import { socketStore } from '@/ui/store/WebSocket';
import { getAvatar } from '@/common/Utils';
import { UserProfileDto, UserSelfProfileDto } from '@/api/common/dto/ResponseDtos';
import { FollowUserCommand, UnfollowUserCommand } from '@/api/websocket/commands/Commands';
import { AppException, AppExceptionCode } from '@/common/AppException';
import { ApiException } from '@/api/http/ApiException';

const { width, thresholds } = useDisplay();
const userState = userStore();
const profileState = profileStore();
const socketState = socketStore();
const activeProfile = computed(() => profileState.getActiveProfile);

const hoveringFollow = ref(false)
const isFollowing = computed(() => {
  const profile = profileState.getActiveProfile
  if (!userState.getUser || !userState.getToken || !profile || profile instanceof UserSelfProfileDto) return false;
  return userState.isFollowing(profile.user.id)
})

const disableFollow = ref(false)

const getUsername = (profile: UserSelfProfileDto | UserProfileDto) => {
  if (profile) {
    if (profile instanceof UserSelfProfileDto) {
      return profile.settings?.username || `${profile.user.discordUsername}#${profile.user.discriminator}`;
    } else if (profile instanceof UserProfileDto) {
      return profile.user.username
    }
    return "Unknown (??)"
  } else {
    return 'Unknown';
  }
}

const followUser = async (user: undefined | any) => {
  if (!user) return
  disableFollow.value = true
  try {
    const command = new FollowUserCommand(user.id);
    const response = await socketState.sendCommand(command);

    if (!response) throw new AppException(AppExceptionCode.NoServerResponse)
    if (response.error || !response.data) {
      const code = response.error?.code || AppExceptionCode.NoServerResponse
      throw ApiException.fromCode(code)
    }

    userState.addFollowing(user)
  } catch (e) {
    console.error("Error following user", e)
  }
  disableFollow.value = false
}

const unfollowUser = async (userId: undefined | string) => {
  if (!userId) return
  disableFollow.value = true
  try {
    const command = new UnfollowUserCommand(userId);
    const response = await socketState.sendCommand(command);

    if (!response) throw new AppException(AppExceptionCode.NoServerResponse)
    if (response.error || !response.data) {
      const code = response.error?.code || AppExceptionCode.NoServerResponse
      throw ApiException.fromCode(code)
    }

    if (activeProfile.value) {
      userState.removeFollowing(activeProfile.value.user.id)
    }
  } catch (e) {
    console.error("Error unfollowing user", e)
  }
  disableFollow.value = false
}
</script>