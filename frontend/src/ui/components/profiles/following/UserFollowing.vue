<template>
  <v-row class="mt-6 mb-0" justify="center">
    <sub-page-header
      :title="$t(`${I18N_KEY}.title`)"
      :subtitle="$t(`${I18N_KEY}.subtitle`)"
      icon="mdi-account-star"
      cols="12"
      lg="11"
    />
  </v-row>
  <v-row justify="space-evenly" v-if="profileState.getActiveProfile?.isLocalUser && userState.getFollowing.length > 0">
    <v-col cols="12" md="11">
      <v-row justify="center">
        <following-card v-for="(user, i) in userState.getFollowing" :key="i" :user="user" />
      </v-row>
    </v-col>
  </v-row>
  <v-row v-else justify="center">
    <no-data :intl="`${I18N_KEY}.noData`" />
  </v-row>
</template>

<script setup lang="ts">
import SubPageHeader from '../common/SubPageHeader.vue';
import FollowingCard from '@/ui/components/profiles/following/FollowingCard.vue';
import NoData from '../common/NoData.vue';

import { profileStore } from '@/ui/store/Profile';
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { userStore } from '@/ui/store/User';

const I18N_KEY = 'pages.profile.following'

const profileState = profileStore();
const userState = userStore();
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