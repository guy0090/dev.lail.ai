<template>
  <v-btn v-if="userState.hasPermission('admin')" :active="activeLink.dashboard" prepend-icon="mdi-shield-crown"
    @click="setActive('dashboard')" class="mx-1" variant="elevated">
    <template v-slot:prepend>
      <v-icon color="red-darken-3"></v-icon>
    </template>
    ADMIN
  </v-btn>
  <v-divider v-if="userState.hasPermission('admin')" class="mx-2" vertical />
  <v-btn :active="activeLink.rankings" prepend-icon="mdi-trophy-variant" @click="setActive('rankings')" class="mx-1">
    <template v-slot:prepend>
      <v-icon color="amber-darken-3"></v-icon>
    </template>
    RANKINGS
  </v-btn>
  <v-btn :active="activeLink.statistics" prepend-icon="mdi-chart-bar" @click="setActive('statistics')" class="mx-1">
    <template v-slot:prepend>
      <v-icon color="green-darken-3"></v-icon>
    </template>
    STATISTICS
  </v-btn>
  <v-btn hidden :active="activeLink.premium" prepend-icon="mdi-star-four-points-outline" @click="setActive('premium')"
    class="mx-1">
    <template v-slot:prepend>
      <v-icon color="light-blue-darken-1"></v-icon>
    </template>
    PREMIUM
  </v-btn>
  <v-btn hidden :active="activeLink.community" prepend-icon="mdi-hammer-sickle" @click="setActive('community')"
    class="mx-1">
    <template v-slot:prepend>
      <v-icon color="red-darken-3"></v-icon>
    </template>
    COMMUNITY
  </v-btn>
  <v-divider class="mx-2" vertical />
</template>


<script setup lang="ts">
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { userStore } from '@/ui/store/User'
import { utilStore } from '@/ui/store/Util';

const router = useRouter()
const userState = userStore()
const utilState = utilStore()

const activeLink = reactive({
  "dashboard": false, // admin
  "rankings": false,
  "statistics": false,
  "premium": false,
  // "community": false
} as { [key: string]: boolean })

const setActive = (name: string, push = true) => {
  Object.keys(activeLink).forEach((key) => {
    if (name === key) {
      activeLink[key] = true
      if (push) router.push({ name: key })
    } else activeLink[key] = false
  })
}

utilState.$onAction(({ name, args }) => {
  if (name === 'setActiveNav') {
    setActive(args[0], false)
  }
})
</script>