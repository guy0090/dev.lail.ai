<template>
  <v-container fluid class="main-content">
    <v-row justify="center" class="content-row">
      <v-col cols="8">
        <v-card>
          <v-card-title>
            <h1 class="display-1">
              <v-icon size="small" color="green" icon="mdi-server" />
              {{ $t('pages.init.title') }}
            </h1>
          </v-card-title>
          <v-card-text>
            <p>
              {{ $t('pages.init.subtitleP1') }}&nbsp;<code>initKey</code>&nbsp;{{ $t('pages.init.subtitleP2') }}
            </p>
            <br>
            <v-img src="/icons/init/key.png" />
            <br>
            <p>{{ $t('pages.init.instructions') }}</p>
            <p v-if="!userState.getUser">
              <br><strong style="color: red">{{ $t('pages.init.loginHint') }}</strong>
            </p>
            <v-form class="mt-5" validate-on="submit" @submit.prevent="submit" :disabled="!userState.getUser">
              <v-text-field clearable v-model="submittedKey" variant="outlined" :rules="rules"
                :label="$t('pages.init.inputLabel')"></v-text-field>
              <v-btn :loading="loading" type="submit" color="green" block class="mt-2"
                :disabled="userState.getUser === null">{{ $t('pages.init.submitLabel') }}</v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

import * as RestApi from '@/api/http/Api'
import { userStore } from '@/ui/store/User'
import { sleep } from '@/common/Utils';

const router = useRouter()
const userState = userStore()
const submittedKey = ref('')
const loading = ref(false)

const rules = [
  (value: string) => submitInitKey(value)
]

const submit = async (event: any) => {
  loading.value = true
  const result = await event
  await sleep(100)
  loading.value = false

  if (result.valid !== undefined && result.valid === true) {
    router.push({ name: "home" })
  }
}

const submitInitKey = async (key: any): Promise<any> => {
  if (!key) return Promise.resolve("Key cannot be empty")
  if ((key as string).length !== 64) return Promise.resolve("Key must be exactly 64 characters long.")

  try {
    await RestApi.initInstance(key as string)
    return true
  } catch (e) {
    return "Invalid key"
  }
}

</script>

<style scoped>
.main-content {
  height: 100%;
}

.content-row {
  height: inherit;
}
</style>