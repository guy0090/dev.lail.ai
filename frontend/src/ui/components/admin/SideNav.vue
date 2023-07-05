<template>
  <v-navigation-drawer permanent absolute>
    <v-list density="compact" nav>
      <v-list-item @click="$router.push({ name: 'dashboard' })">
        Admin Area
        <template v-slot:prepend>
          <v-icon color="red-accent-3" icon="mdi-wrench" />
        </template>
      </v-list-item>
    </v-list>

    <v-divider />

    <v-list density="compact" nav>
      <v-list-subheader>Dashboard</v-list-subheader>
      <v-list-item prepend-icon="mdi-view-carousel-outline" :active="activeLink.dashboard" @click="setActive('dashboard')">
        <v-list-item-title>Overview</v-list-item-title>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter()
const activeLink = reactive({
  "dashboard": false,
} as { [key: string]: boolean })

const setActive = (name: string) => {
  Object.keys(activeLink).forEach((key) => {
    if (name === key) {
      activeLink[key] = true
      router.push({ name: key })
    } else activeLink[key] = false
  })
}

onMounted(() => {
  const route = useRoute()
  setActive(route.name?.toString() ?? 'dashboard')
})
</script>