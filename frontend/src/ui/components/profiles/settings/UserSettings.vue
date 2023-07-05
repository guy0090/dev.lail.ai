<template>
  <v-row class="mt-6 mb-0" justify="center">
    <sub-page-header
      :title="$t(`${I18N_KEY}.title`)"
      icon="mdi-cog"
      cols="12"
      lg="11"
    />
  </v-row>
  <v-row class="mt-0" justify="center" v-if="profileState.getActiveProfile?.isLocalUser">
    <v-col cols="12" lg="11">
      <v-card class="rounded px-5 pt-7 pb-10" border>
        <username-settings />
        <custom-url-settings />
        <privacy-settings />
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import UsernameSettings from './UsernameSettings.vue';
import PrivacySettings from './PrivacySettings.vue';
import CustomUrlSettings from './CustomUrlSettings.vue';
import SubPageHeader from '../common/SubPageHeader.vue';

import { onMounted } from 'vue';
import { profileStore } from '@/ui/store/Profile';
import { useRoute, useRouter } from 'vue-router';
import { userStore } from '@/ui/store/User';

const I18N_KEY = 'pages.profile.settings';

const userState = userStore();
const profileState = profileStore();
const route = useRoute();
const router = useRouter();

const isOwnProfile = (id: string) => {
  return (userState.getUser && userState.getUser.id === id) || /^@?me$/i.test(id)
}

onMounted(() => {
  const param = route.params.id as string;
  if (userState.getUser && isOwnProfile(param)) return

  const profile = profileState.getActiveProfile;
  if (!profile || !profile.isLocalUser) {
    const params = route.params
    router.push({ name: 'userUploads', params })
  }
})

</script>