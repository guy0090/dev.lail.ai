<template>
  <v-list nav lines="one" class="bg-grey-darken-4 px-0 pt-1">
    <v-list-item border v-if="userState.hasPermission('admin')" :active="activeLink.dashboard" @click="setActive('dashboard')">
      <template v-slot:prepend>
        <v-icon class="ms-3 me-4" color="red">mdi-shield-crown</v-icon>
      </template>
      Admin
    </v-list-item>
    <v-divider v-if="userState.hasPermission('admin')" class="my-3"/>

    <v-list-item class="mb-2" border :active="activeLink.rankings" @click="setActive('rankings')">
      <template v-slot:prepend>
        <v-icon class="ms-3 me-4" color="amber-darken-3">mdi-trophy-variant</v-icon>
      </template>
      Rankings
    </v-list-item>

    <v-list-item class="mb-2" border :active="activeLink.statistics" @click="setActive('statistics')">
      <template v-slot:prepend>
        <v-icon class="ms-3 me-4" color="green-darken-3">mdi-chart-bar</v-icon>
      </template>
      Statistics
    </v-list-item>
  </v-list>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue';
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
  "community": false
} as { [key: string]: boolean })

const setActive = (name: string, push = true) => {
  Object.keys(activeLink).forEach((key) => {
    if (name === key) {
      activeLink[key] = true
      if (push) router.push({ name: (key === 'dashboard' ? 'dashboard' : 'home') })
    } else activeLink[key] = false
  })
}

utilState.$onAction(({ name, args }) => {
  if (name === 'setActiveNav') {
    setActive(args[0], false)
  }
})

onMounted(() => {
  const nav = utilState.activeMainNav
  if (activeLink[nav] !== undefined) setActive(nav, false)
})
</script>