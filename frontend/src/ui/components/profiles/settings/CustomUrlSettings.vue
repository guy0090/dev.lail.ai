<template>
  <v-row>
    <v-col cols="12" md="12" lg="8">
      <v-row>
        <v-col>
          <h2>
            <v-icon icon="mdi-link" class="me-2" />{{ $t(`${I18N_KEY}.title`) }}
            <v-btn class="ms-2" variant="tonal" color="red" prepend-icon="mdi-refresh" :disabled="disabled"
              @click="resetCustomUrl()" :hidden="!customUrl || customUrl === ''">
              {{ $t(`${I18N_KEY}.resetButtonLabel`) }}
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
            ref="slugRef"
            density="compact"
            :bg-color="!showResultIcon ? 'grey-darken-3' : saveSuccess ? 'green-accent-4' : 'red-accent-4'"
            :prefix="$t(`${I18N_KEY}.inputPrefix`, { url: 'lail.ai' })"
            prepend-inner-icon="mdi-pencil"
            :append-inner-icon="!showResultIcon ? 'mdi-content-save' : saveSuccess ? 'mdi-check' : 'mdi-close'"
            :hint="$t(`${I18N_KEY}.inputHint`, { min: MIN_LENGTH, max: MAX_LENGTH })"
            v-model="customUrl"
            :loading="loading" :disabled="disabled" :label="getDiscordName()" @click:append-inner="saveCustomUrl"
            :rules="[urlRules.counter, urlRules.rgx]"
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

const I18N_KEY = 'pages.profile.settings.urlSlug'

const MIN_LENGTH = 2
const MAX_LENGTH = 20
const REGEX = RegExp(`^[a-zA-Z0-9_]{${MIN_LENGTH},${MAX_LENGTH}}$`)

const userState = userStore()
const profileState = profileStore()

const disabled = ref(false)
const loading = ref(false)
const saveSuccess = ref(true)
const showResultIcon = ref(false)

const slugRef = ref()

const getCustomUrl = () => {
  const profile = profileState.getActiveProfile as UserSelfProfileDto
  return profile?.settings?.customUrlSlug
}

const getDiscordName = () => {
  return userState.getDiscordUsername
}

const customUrl = ref(getCustomUrl())
if (!customUrl.value && !profileState.getActiveProfile) {
  disabled.value = true
  loading.value = true
}

const urlRules = {
  counter: (value: any) => (value.length >= MIN_LENGTH && value.length <= MAX_LENGTH)
    || `Must be ${MIN_LENGTH} to ${MAX_LENGTH} characters long.`,
  rgx: (value: any) => REGEX.test(value)
    || "Must only contain alphanumeric characters and/or underscores",
}

const resetCustomUrl = () => {
  loading.value = true
  disabled.value = true
  slugRef.value?.reset()
  userState.resetSetting("customUrlSlug").then(() => {
    loading.value = false
    disabled.value = false
  })
}

const saveCustomUrl = async () => {
  const validation = await slugRef.value?.validate()
  if (validation?.length !== 0) return

  loading.value = true;
  disabled.value = true;
  const update = new UserSettingsUpdateDto({ customUrlSlug: customUrl.value });

  let result = false
  try {
    result = await userState.updateSetting(update);
  } catch (e: any) {
    saveSuccess.value = false;
  }
  saveSuccess.value = result

  loading.value = false;
  showResultIcon.value = true;
  await sleep(1500)
  showResultIcon.value = false;
  disabled.value = false;
}

// In the case data is still loading, we wait until it finishes
// and update the ref value of the setting
userState.$onAction(({ name, args }) => {
  if (name === "setSettings") {
    customUrl.value = args[0].customUrlSlug
  }
})
profileState.$onAction(({ name, args }) => {
  if (name === "setActiveProfile" && args[0] instanceof UserSelfProfileDto) {
    customUrl.value = args[0].settings?.customUrlSlug
    loading.value = false
    disabled.value = false
  }
})
</script>