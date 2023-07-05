<template>
  <v-row>
    <v-col cols="12" md="12" lg="8">
      <v-row>
        <v-col>
          <h2>
            <v-icon icon="mdi-incognito" class="me-2" />{{ $t(`${I18N_KEY}.title`) }}
            <v-btn class="ms-2" :hidden="!didSettingChange()" :color="saveSuccess ? 'green' : 'red'" variant="tonal"
              :loading="loading" @click="savePrivacySettings">
              <p v-if="!showSaveIcon">{{ $t(`${I18N_KEY}.saveButtonLabel`) }}</p>
              <v-icon v-else-if="showSaveIcon && saveSuccess" icon="mdi-check" />
              <v-icon v-else-if="showSaveIcon && !saveSuccess" icon="mdi-close" />
            </v-btn>
          </h2>
          <strong style="color: grey">{{ $t(`${I18N_KEY}.subtitle`) }}</strong>
        </v-col>
      </v-row>

      <v-row class="mt-0">
        <v-col cols="auto" class="pa-1">
          <v-container fluid class="pa-0 pt-3">
            <v-radio-group prepend-icon="mdi-cloud-upload" :label="$t(`${I18N_KEY}.types.uploads`)" inline color="success"
              v-model="uploadPrivacy" :disabled="disabled" class="ms-3">
              <v-radio class="c-label" :label="$t(`${I18N_KEY}.labels.${PrivacySetting.PUBLIC}`)" :value="PrivacySetting.PUBLIC" />
              <v-radio class="c-label" :label="$t(`${I18N_KEY}.labels.${PrivacySetting.PRIVATE}`)" :value="PrivacySetting.PRIVATE" />
              <v-radio class="c-label" :label="$t(`${I18N_KEY}.labels.${PrivacySetting.UNLISTED}`)" :value="PrivacySetting.UNLISTED" />
            </v-radio-group>
            <v-radio-group prepend-icon="mdi-account" :label="$t(`${I18N_KEY}.types.profile`)" hide-details inline color="success"
              v-model="profilePrivacy" :disabled="disabled" class="ms-3">
              <v-radio class="c-label" :label="$t(`${I18N_KEY}.labels.${PrivacySetting.PUBLIC}`)" :value="PrivacySetting.PUBLIC" />
              <v-radio class="c-label" :label="$t(`${I18N_KEY}.labels.${PrivacySetting.PRIVATE}`)" :value="PrivacySetting.PRIVATE" />
              <v-radio class="c-label" :label="$t(`${I18N_KEY}.labels.${PrivacySetting.UNLISTED}`)" :value="PrivacySetting.UNLISTED" />
            </v-radio-group>
          </v-container>
        </v-col>
      </v-row>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { userStore } from '@/ui/store/User';
import { profileStore } from "@/ui/store/Profile";
import PrivacySetting from "@/api/common/enums/PrivacySetting";
import { UserSettingsUpdateDto } from "@/api/common/dto/RequestDtos";
import { sleep } from "@/common/Utils";
import { UserSelfProfileDto } from "@/api/common/dto/ResponseDtos";

const userState = userStore();
const profileState = profileStore();

const I18N_KEY = "pages.profile.settings.privacy";

const disabled = ref(false)
const loading = ref(false)
const showSaveIcon = ref(false)
const saveSuccess = ref(true)

const getProfile = () => {
  return profileState.getActiveProfile as UserSelfProfileDto;
}

const uploadPrivacy = ref(getProfile()?.settings?.uploadPrivacySetting);
const profilePrivacy = ref(getProfile()?.settings?.profilePrivacySetting);

if (uploadPrivacy.value === undefined || profilePrivacy.value === undefined) {
  loading.value = true;
  disabled.value = true;
}

const didSettingChange = () => {
  const profile = profileState.getActiveProfile;
  if (profile && profile instanceof UserSelfProfileDto) {
    const profilePrivacySetting = profile.settings?.profilePrivacySetting;
    const uploadPrivacySetting = profile.settings?.uploadPrivacySetting;
    return uploadPrivacySetting !== uploadPrivacy.value || profilePrivacySetting !== profilePrivacy.value;
  }
}

const savePrivacySettings = async () => {
  disabled.value = true;
  loading.value = true;
  showSaveIcon.value = true;
  const update = new UserSettingsUpdateDto({
    uploadPrivacySetting: uploadPrivacy.value,
    profilePrivacySetting: profilePrivacy.value
  });

  try {
    await userState.updateSetting(update);
    saveSuccess.value = true;
  } catch (e: any) {
    saveSuccess.value = false;
    console.error("Failed to update privacy setting", update, e);
  }

  loading.value = false;
  await sleep(1500);
  showSaveIcon.value = false;
  disabled.value = false;

}

// In the case data is still loading, we wait until it finishes
// and update the ref value of the setting
userState.$onAction(({ name, args }) => {
  if (name === "setSettings") {
    uploadPrivacy.value = args[0].uploadPrivacySetting
    profilePrivacy.value = args[0].profilePrivacySetting
    loading.value = false;
    disabled.value = false;
  }
})

profileState.$onAction(({ name, args }) => {
  if (name === "setActiveProfile" && args[0] instanceof UserSelfProfileDto) {
    uploadPrivacy.value = args[0].settings?.uploadPrivacySetting
    profilePrivacy.value = args[0].settings?.profilePrivacySetting
    loading.value = false;
    disabled.value = false;
  }
})
</script>

<style>
.c-label > .v-label {
  opacity: 1 !important;
}

.c-radio-group >.v-input__control > .v-selection-control-group {
  margin-top: 0 !important;
}
</style>
