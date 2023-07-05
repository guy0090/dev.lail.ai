<template>
  <v-row justify="center" class="mt-0">
    <v-col cols="12" lg="11" class="py-0">
      <v-card class="rounded-0 rounded-b border-s border-e border-b">
        <v-tabs bg-color="grey-darken-3" :model-value="activeNav" height="40"
          @update:model-value="changeActive" grow show-arrows
        >
          <v-tab :value="navValues[0]" :active="navValues[0].active" prepend-icon="mdi-cloud-upload"
            @click="$router.push({ name: 'userUploads' })">{{ $t(`${I18N_KEY}.uploads`) }}
          </v-tab>

          <template v-if="profileState.getActiveProfile?.isLocalUser">
            <v-divider vertical />
            <v-tab :value="navValues[1]" :active="navValues[1].active"
              prepend-icon="mdi-account-star" @click="$router.push({ name: 'userFollowing' })">{{
                $t(`${I18N_KEY}.following`) }}
            </v-tab>
            <v-divider vertical />
            <v-tab :value="navValues[3]" :active="navValues[3].active"
              prepend-icon="mdi-cog" @click="$router.push({ name: 'userSettings' })">{{ $t(`${I18N_KEY}.settings`) }}
            </v-tab>
            <!-- <v-divider vertical /> -->
          </template>

          <v-tab hidden :value="navValues[2]" :active="navValues[2].active" prepend-icon="mdi-shield"></v-tab>
        </v-tabs>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { onBeforeRouteUpdate, useRoute } from 'vue-router';

import { profileStore } from '@/ui/store/Profile';

const I18N_KEY = 'pages.profile.nav';

const route = useRoute();
const profileState = profileStore();

const navValues = [
  {
    name: "userUploads",
    active: false,
  },
  {
    name: "userFollowing",
    active: false,
  },
  {
    name: "userBadges",
    active: false,
  },
  {
    name: "userSettings",
    active: false,
  },
]
const activeNav = ref(navValues[0])

onMounted(() => {
  const nav = navValues.find((nav) => nav.name === route.name?.toString())
  if (nav) {
    nav.active = true
    activeNav.value = nav
  } else {
    activeNav.value = navValues[0]
  }
})

const changeActive = (event: any) => {
  navValues.forEach(nav => nav.active = false)

  event.active = true
  activeNav.value = event
}

onBeforeRouteUpdate((to) => {
  const nav = navValues.find((nav) => nav.name === to.name?.toString())
  if (nav) changeActive(nav)
})
</script>


