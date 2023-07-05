<template>
  <v-row justify="center" v-if="!userState.getUser && !loginFailed">
    <v-col cols="auto">
      <h1>{{ $t('pages.login.title') }}</h1>
    </v-col>
  </v-row>
  <login-failed-card :error="error" v-else-if="loginFailed === true" />
  <logged-in-card v-else />
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import * as RestApi from '@/api/http/Api'

import LoginFailedCard from '@/ui/components/login/LoginFailed.vue'
import LoggedInCard from '@/ui/components/login/LoggedInCard.vue'

import { userStore } from '@/ui/store/User'
import { socketStore } from '@/ui/store/WebSocket'
import { utilStore } from '@/ui/store/Util'
import { useRoute, useRouter } from 'vue-router'
import type { UserDetailsDto } from '@/api/common/dto/ResponseDtos'
import { getApiException, sleep } from '@/common/Utils'
import { ApiException, ExceptionCode } from '@/api/http/ApiException'

const userState = userStore()
const socketState = socketStore()
const utilState = utilStore()
const route = useRoute()
const router = useRouter()

const retries = reactive({
  current: 0,
  max: 5,
})
const code = ref(route.query.code as string | undefined)
const loginFailed = ref(false)
const error = ref(new Error("Unknown error"))

onMounted(() => {
  if (!code.value || code.value === "" || userState.getUser) {
    router.push({ name: "home" })
  } else {
    // console.log("Registering with code: ", code.value)
    register(code.value)
  }
})

const register = async (code: string | undefined) => {
  if (!code || code === "") return
  utilState.toggleLoading()

  let loginData: UserDetailsDto | undefined = undefined
  while (retries.current < retries.max) {
    try {
      loginData = await RestApi.register(code)
      break
    } catch (e: any) {
      error.value = getApiException(e as Error)
      if (error.value instanceof ApiException && error.value.code === ExceptionCode.SIGN_UPS_DISABLED) {
        loginFailed.value = true
        break
      }

      console.error(`Login failed with error ${e} - retrying...`)
      retries.current++
    }
    await sleep(1000)
  }

  utilState.toggleLoading()
  if (!loginData) {
    loginFailed.value = true
  } else {
    userState.updateInfo(loginData)
    socketState.reconnect()
    await sleep(2000)
    router.push({ name: 'userUploads', params: { id: 'me' } })
  }
}

</script>
