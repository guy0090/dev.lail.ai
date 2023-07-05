<template>
  <v-btn v-if="!userState.getUser" :block="block" class="login-btn bg-indigo-accent-3" prepend-icon="mdi-discord" rounded="sm"
    variant="flat" @click="toDiscordOAuth()">
    Login
  </v-btn>
</template>

<script setup lang="ts">
import { toDiscordOAuth } from '@/api/http/Api'
import { userStore } from '@/ui/store/User'

defineProps({
  block: {
    type: Boolean,
    required: false,
    default: false,
  }
})

const userState = userStore()
</script>