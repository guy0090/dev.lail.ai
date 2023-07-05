<template>
  <v-dialog
      v-model="menuDialog"
      fullscreen
      :scrim="false"
      :transition="false"
      close-on-content-click
    >
      <template v-slot:activator="{ props }">
        <v-btn icon="mdi-menu" v-bind="props" />
        <v-chip variant="elevated" rounded="sm" class="ms-2"
          size="small" color="red-darken-3">ALPHA
        </v-chip>
      </template>
      <v-card class="bg-grey-darken-4">
        <v-toolbar height="64" density="comfortable" elevation="0" class="border-b" color="grey-darken-4">
          <v-img :lazy="false" class="ms-5 nav-icon" src="/icons/mokoko.png" max-height="40" max-width="40"
            contain @click="$router.push({ name: 'home' })" />
            <v-btn icon="mdi-menu" @click="menuDialog = false"/>
            <v-chip variant="elevated" rounded="sm" class="ms-2"
            size="small" color="red-darken-3">ALPHA
            </v-chip>
          <v-spacer />
          <login-button class="ms-5 me-3" v-if="!userState.getUser" />
          <logged-in :dialog-menu="true" v-else />
        </v-toolbar>
        <v-card-text>
          <dialog-nav-links />
        </v-card-text>
        <v-card-actions >
          <v-btn v-if="userState.getUser" variant="elevated" block color="red" @click="menuDialog = false">Logout</v-btn>
          <login-button :block="true" v-else />
        </v-card-actions>
      </v-card>
    </v-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { userStore } from '@/ui/store/User';

import LoginButton from "@/ui/components/common/navigation/LoginButton.vue";
import DialogNavLinks from './DialogNavLinks.vue';
import LoggedIn from "@/ui/components/common/navigation/LoggedInUser.vue"

const menuDialog = ref(false)
const userState = userStore()
</script>