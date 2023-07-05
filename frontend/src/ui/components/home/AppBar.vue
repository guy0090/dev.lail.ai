<template>
  <v-app-bar density="comfortable" elevation="1" class="border-b">
    <v-img class="ms-5 nav-link" src="/icons/mokoko.png" max-height="40" max-width="40"
      contain @click="$router.push({ name: 'home' })" />

    <v-toolbar-title class="ms-2" style="user-select: none;"
      v-if="thresholds.md < width">Lost Ark Logs&nbsp;
        <v-chip variant="elevated" rounded="sm" size="small" color="red-darken-3">ALPHA</v-chip>
      </v-toolbar-title>
    <dialog-nav v-else />

    <template v-slot:append v-if="thresholds.md < width">
      <navigation-links />
      <login-button class="ms-5 me-5" v-if="!userState.getUser" />
      <logged-in class="me-4" v-else />
    </template>
    <template v-slot:append v-else>
      <login-button class="ms-5" v-if="!userState.getUser" />
      <logged-in v-else />
    </template>
  </v-app-bar>
</template>

<script setup lang="ts">
import { useDisplay } from 'vuetify'
import DialogNav from '@/ui/components/common/navigation/DialogNav.vue';
import LoginButton from "@/ui/components/common/navigation/LoginButton.vue";
import NavigationLinks from "@/ui/components/common/navigation/NavigationLinks.vue";
import LoggedIn from "@/ui/components/common/navigation/LoggedInUser.vue"

import { userStore } from '@/ui/store/User'

const { thresholds, width } = useDisplay()
const userState = userStore()
</script>

<style scoped>
.nav-link:hover {
  cursor: pointer;
}
</style>