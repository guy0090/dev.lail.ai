<template>
  <v-app style="overflow-y: auto">
    <app-bar />

    <v-main>
      <v-progress-linear model-value="100" :height="utilState.getLoading ? '3' : '0'" indeterminate
        color="teal"></v-progress-linear>
      <v-container fluid v-if="backendState.isBackendReady" style="height:100%">
        <v-row justify="center" style="height:inherit">
          <v-col cols="12" xl="8">
            <!-- Main Content -->
            <v-container fluid class="pt-1">

              <!-- Alert Space -->
              <v-row hidden justify="center">
                <v-col cols="auto">
                  <v-alert color="teal-darken-3" title="Early Alpha" icon="mdi-alert">
                    <template v-slot:text>
                      This is an early alpha rework of <a href="https://lail.ai">lail.ai</a> - expect frequent (breaking) changes.
                    </template>
                  </v-alert>
                </v-col>
              </v-row>
              <!-- /Alert Space -->

              <!-- Pages -->
              <router-view />
              <!-- /Pages -->
            </v-container>
            <!-- /Main Content -->
          </v-col>
        </v-row>
      </v-container>
      <init-page v-else />
    </v-main>

    <footer-area v-if="!isAdminRoute" />
  </v-app>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

import AppBar from '@/ui/components/home/AppBar.vue'
import FooterArea from '@/ui/components/home/FooterArea.vue'
import InitPage from '@/ui/pages/InitPage.vue'

import { socketStore } from '@/ui/store/WebSocket'
import { backendStore } from '@/ui/store/Backend'
import { utilStore } from '@/ui/store/Util'
import { userStore } from '@/ui/store/User'

import { WebSocketEvent } from '@/api/websocket/EventTypes'
// import type { NotificationReceived } from '@/api/websocket/messages/Messages'
import { GetFollowingCommand, GetNotificationsCommand } from '@/api/websocket/commands/Commands'
import { notificationStore } from './store/Notification'
import { AppException, AppExceptionCode } from '@/common/AppException'
import { ApiException } from '@/api/http/ApiException'
import { computed } from 'vue'

const router = useRouter()
const socketState = socketStore()
const backendState = backendStore()
const utilState = utilStore()
const userState = userStore()
const notificationState = notificationStore()

const baseRoute = computed(() => router.currentRoute.value.path.split('/')[1])
const isAdminRoute = computed(() => baseRoute.value === 'admin')

const getNotifications = async () => {
  const command = new GetNotificationsCommand();
  const response = await socketState.sendCommand(command);

  if (!response) throw new AppException(AppExceptionCode.NoServerResponse)
  if (response.error || !response.data) {
    const code = response.error?.code || AppExceptionCode.NoServerResponse
    throw ApiException.fromCode(code)
  }

  return command.getResult(response.data).result
}

const getFollowing = async () => {
  const command = new GetFollowingCommand();
  const response = await socketState.sendCommand(command);

  if (!response) throw new AppException(AppExceptionCode.NoServerResponse)
  if (response.error || !response.data) {
    const code = response.error?.code || AppExceptionCode.NoServerResponse
    throw ApiException.fromCode(code)
  }

  return command.getResult(response.data).result
}

onMounted(() => {
  socketState.$onAction(({ name, args }) => {
    if (name === "messageReceived") {
      const data = args[0]

      const currentRouteName = router.currentRoute.value.name
      switch (data.getEvent()) {
        case WebSocketEvent.Welcome:
          backendState.handleWelcomeEvent(data)
          if (!backendState.isBackendReady && currentRouteName !== "login") router.push({ name: "init" })
          break;
        case WebSocketEvent.NotificationReceived:
          // var notification = data as NotificationReceived
          // notificationState.addNotification(notification.getNotification())
          break;
        case WebSocketEvent.UserDetailsChanged:
          userState.updateUserDetails(data)
          break;
        default:
          console.warn("Unhandled socket event", data)
          break;
      }
    }
  })

  let ticks = 0
  const maxTicks = 20 // 2 seconds
  userState.$onAction(({ name }) => {
    if (name === "setLoggedIn") {
      // Socket connects after login, so we need to wait for it to connect
      const socketWait = setInterval(() => {
        if (ticks >= maxTicks) {
          clearInterval(socketWait)
          console.error("Socket did not connect in time")
        }
        if (socketState.isConnected) {
          clearInterval(socketWait)

          getFollowing().then((following) => {
            userState.setFollowing(following)
          }).catch(err => {
            console.error("Failed to get following", err)
          })

          getNotifications().then(notifications => {
            notificationState.setNotifications(notifications)
          }).catch(err => {
            console.error("Failed to get notifications", err)
          })
        }
        ticks++
      }, 100)
    }
  })
})
</script>

<style>
html {
  overflow-y: auto !important;
}

.no-select {
  user-select: none !important;
}

.hr-sep {
  height: 0px !important;
  border-width: 1px;
  border-color: rgba(255, 255, 255, 0.12);
}

/* width */
::-webkit-scrollbar {
  width: 5px;
}

/* Track */
::-webkit-scrollbar-track {
  background: #121212;
}

/* Handle */
::-webkit-scrollbar-thumb {
  background: #888;
}

/* Handle on hover */
::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>