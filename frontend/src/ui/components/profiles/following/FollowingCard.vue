<template>
  <v-col cols="auto" class="following-card">
    <v-card rounded border height="182" max-width="500" link @click="goToProfile(user.id)">
      <v-card-text class="pa-0 me-6">
          <v-row>
            <v-col cols="auto" class="pe-1">
              <v-img cover width="128" height="128" class="border-e" :src="getAvatar(user)"></v-img>
            </v-col>
            <v-col class="ps-5" align-self="center" style="max-width: calc(500px - 182px)">
              <v-row class="mb-1 mt-1">
                <span class="text-h5 d-inline-block text-truncate">{{ user.username }}</span>
              </v-row>
            </v-col>
          </v-row>
        </v-card-text>
        <v-divider />
        <v-card-actions>
          <v-btn color="blue" variant="elevated" size="small"
          prepend-icon="mdi-account-cancel" @click="clickUnfollow(user.id)">
            {{ $t(`${I18N_KEY}.unfollowButtonLabel`) }}
          </v-btn>
        </v-card-actions>
    </v-card>
  </v-col>
</template>

<script setup lang="ts">
import type { UserDto } from '@/api/common/dto/ResponseDtos';
import { UnfollowUserCommand } from '@/api/websocket/commands/Commands';
import { getAvatar } from '@/common/Utils';
import { ref, computed } from 'vue';
import { userStore } from '@/ui/store/User';
import { profileStore } from '@/ui/store/Profile';
import { socketStore } from '@/ui/store/WebSocket';
import { useRouter } from 'vue-router';
import { ApiException } from '@/api/http/ApiException';
import { AppException, AppExceptionCode } from '@/common/AppException';

defineProps({
  user: {
    type: Object as () => UserDto,
    required: true
  }
})


const I18N_KEY = 'pages.profile.following';

const userState = userStore()
const profileState = profileStore()
const socketState = socketStore()
const router = useRouter()

const activeProfile = computed(() => profileState.getActiveProfile)

const disableButton = ref(false)
const clickedButton = ref(false)

const goToProfile = (id: string) => {
  if (clickedButton.value) return
  router.push({ name: 'userUploads', params: { id } })
}

const clickUnfollow = (id: string) => {
  clickedButton.value = true
  unfollowUser(id).then(() => {
    userState.removeFollowing(id)
    clickedButton.value = false
  }).catch((e) => {
    console.error("Error unfollowing user", e)
    clickedButton.value = false
  })
}

const unfollowUser = async (userId: string) => {
  disableButton.value = true
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
  disableButton.value = false
}
</script>

<style scoped>
.following-card > * {
  user-select: none;
}
</style>