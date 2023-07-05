<template>
  <v-row>
    <v-col cols="12" md="12" lg="8">
      <v-row>
        <v-col>
          <h2>
            <v-icon icon="mdi-label" class="me-2" />{{ $t(`${I18N_KEY}.title`) }}
            <v-btn class="ms-2" variant="tonal" color="red" prepend-icon="mdi-refresh" :disabled="disabled"
              @click="resetUsername()" :hidden="!customUsername || customUsername === ''">{{
                $t(`${I18N_KEY}.resetButtonLabel`) }}
            </v-btn>
          </h2>
          <strong style="color: gray">{{ $t(`${I18N_KEY}.subtitle`) }}</strong>
        </v-col>
      </v-row>
      <v-row class="mt-0">
        <v-col class="pa-1">
          <!-- A T T R I B U T E S -->
          <v-text-field
            single-line
            variant="solo"
            ref="usernameRef"
            density="compact"
            :bg-color="!showResultIcon ? 'grey-darken-3' : saveSuccess ? 'green-accent-4' : 'red-accent-4'"
            prepend-inner-icon="mdi-pencil"
            :append-inner-icon="!showResultIcon ? 'mdi-content-save' : saveSuccess ? 'mdi-check' : 'mdi-close'"
            :hint="$t(`${I18N_KEY}.inputHint`, { min: MIN_LENGTH, max: MAX_LENGTH })" v-model="customUsername"
            :label="getDiscordName()" :loading="loading" :disabled="disabled" @click:append-inner="saveUsername"
            :rules="[usernameRules.counter, usernameRules.rgx]"
            style="max-width: 500px"></v-text-field>
        </v-col>
      </v-row>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { ref } from 'vue';

import { userStore } from '@/ui/store/User';
import { profileStore } from '@/ui/store/Profile';
import { UserSettingsUpdateDto } from '@/api/common/dto/RequestDtos';
import { sleep } from '@/common/Utils';
import { UserSelfProfileDto } from '@/api/common/dto/ResponseDtos';

const I18N_KEY = 'pages.profile.settings.username'

const MIN_LENGTH = 2
const MAX_LENGTH = 32
const REGEX = RegExp(`^\\S{${MIN_LENGTH},${MAX_LENGTH}}$`)

const userState = userStore()
const profileState = profileStore()

const disabled = ref(false)
const loading = ref(false)
const saveSuccess = ref(true)
const showResultIcon = ref(false)

const usernameRef = ref()

const getCustomUsername = () => {
  const profile = profileState.getActiveProfile as UserSelfProfileDto
  return profile?.settings?.username
}

const getDiscordName = () => {
  return userState.getDiscordUsername
}

const customUsername = ref(getCustomUsername())
if (!customUsername.value && !profileState.getActiveProfile) {
  loading.value = true
  disabled.value = true
}

const usernameRules = {
  counter: (value: any) => (value && value.length >= MIN_LENGTH && value.length <= MAX_LENGTH)
    || `Must be ${MIN_LENGTH} to ${MAX_LENGTH} characters long.`,
  rgx: (value: any) => REGEX.test(value)
    || "Must only contain alphanumeric characters and/or underscores.",
}

const resetUsername = () => {
  loading.value = true
  disabled.value = true
  usernameRef.value?.reset()
  userState.resetSetting("username").then(() => {
    loading.value = false
    disabled.value = false
  })
}

const saveUsername = async () => {
  const validation = await usernameRef.value?.validate()
  if (validation?.length !== 0) return

  loading.value = true;
  disabled.value = true;
  const update = new UserSettingsUpdateDto({ username: customUsername.value });

  let result = false
  try {
    result = await userState.updateSetting(update);
  } catch (e: any) {
    saveSuccess.value = false;
  }
  saveSuccess.value = result

  loading.value = false;
  showResultIcon.value = true;
  await sleep(1500);
  showResultIcon.value = false;
  disabled.value = false;
}

// In the case data is still loading, we wait until it finishes
// and update the ref value of the setting
userState.$onAction(({ name, args }) => {
  if (name === "setSettings") {
    customUsername.value = args[0].username
    loading.value = false;
    disabled.value = false;
  }
})
profileState.$onAction(({ name, args }) => {
  if (name === "setActiveProfile" && args[0] instanceof UserSelfProfileDto) {
    customUsername.value = args[0].settings?.username
    loading.value = false
    disabled.value = false
  }
})

</script>