<template>
  <div class="text-center">
    <v-menu v-model="notifMenu" :close-on-content-click="true" location="bottom">
      <template v-slot:activator="{ props }">
        <v-btn class="mx-1" icon v-bind="props">
          <v-badge dot color="red-accent-3" v-if="hasUnseenNotifications()">
            <v-icon :icon="notifMenu ? 'mdi-bell' : 'mdi-bell-outline'" />
          </v-badge>
          <v-icon v-else :icon="notifMenu ? 'mdi-bell' : 'mdi-bell-outline'"  />
        </v-btn>
      </template>
      <v-card min-width="275" max-width="400" max-height="500" border>
        <v-list :close-on-content-click="true" class="pt-0">
          <v-list-item class="pe-4" title="Notifications">
            <template v-slot:append>
              <v-btn size="sm" variant="flat" icon="mdi-cog-outline" @click="$router.push({ name: 'userSettings', params: { id: 'me' } })" />
            </template>
          </v-list-item>
          <v-divider class="mb-2" />
          <template v-if="getNotifications().length === 0">
            <v-list-item>
              <v-list-item-title>All caught up</v-list-item-title>
            </v-list-item>
          </template>
          <template v-else>
            <notification-item v-for="(notification, i) in getNotifications().sort((a, b) => b.timestamp - a.timestamp)"
              :key="i" :notification="notification" />
          </template>
        </v-list>
      </v-card>
    </v-menu>
    <v-menu v-if="!dialogMenu" v-model="menu" close-on-content-click location="bottom">
      <template v-slot:activator="{ props }">
        <v-btn class="me-3" icon v-bind="props">
          <v-avatar :image="userState.getUserAvatar()" />
        </v-btn>
      </template>
      <v-card min-width="275" border>
        <v-list>
          <v-list-item :prepend-avatar="userState.getUserAvatar()"
            :title="userState.getSettings?.username || userState.getUser?.discordUsername"
            :subtitle="userState.getUser?.discordUsername + '#' + userState.getUser?.discriminator">
          </v-list-item>
        </v-list>
        <v-divider />
        <v-list>
          <v-list-item prepend-icon="mdi-account"
            @click="$router.push({ name: 'userUploads', params: { id: 'me' } })">Profile</v-list-item>
          <v-list-item prepend-icon="mdi-logout-variant" class="text-red-accent-3" @click="userState.logout()">
            Logout
          </v-list-item>
        </v-list>
      </v-card>
    </v-menu>
    <v-btn class="me-3" icon v-else>
      <v-avatar :image="userState.getUserAvatar()" @click="$router.push({ name: 'userUploads', params: { id: 'me' } })" />
    </v-btn>
  </div>
</template>

<script lang="ts" setup>
import NotificationItem from '../notifications/NotificationItem.vue';

import { ref } from 'vue'

import { userStore } from '@/ui/store/User';
import { notificationStore } from '@/ui/store/Notification';

defineProps({
  dialogMenu: {
    type: Boolean,
    required: false,
    default: false,
  }
})

const notifMenu = ref(false)
const menu = ref(false)
const userState = userStore()
const notificationState = notificationStore()

const getNotifications = () => {
  return notificationState.getNotifications
}

const hasUnseenNotifications = () => {
  return getNotifications().some(n => !n.seen)
}
</script>