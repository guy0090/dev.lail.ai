<template>
  <v-list-item nav hover :prepend-avatar="avatar" @click="handleClick" class="px-4 py-1">
    <v-list-item-title>{{ title }}</v-list-item-title>
    <v-list-item-subtitle>{{ subtitle }}</v-list-item-subtitle>
    <template v-slot:append v-if="!seen">
      <v-badge dot color="green-accent-3" inline class="ms-2"></v-badge>
    </template>
  </v-list-item>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import type { Notification } from '@/api/common/Notification'
import { notificationStore } from '@/ui/store/Notification';

const notificationState = notificationStore()

const props = defineProps<{
  notification: Notification
}>()

const id = ref("")
const title = ref("")
const subtitle = ref("")
const avatar = ref("")
const seen = ref(false)

const handleClick = async () => {
  // Set it as seen on first click, second click will clear it
  // TODO: Do this a different way?
  try {
    if (!seen.value) {
      await notificationState.setNotificationSeen(id.value)
      seen.value = true
    } else {
      await notificationState.clearNotification(id.value)
    }
  } catch (e) {
    console.error(e)
  }

  switch (props.notification.type) {
    default:
      // TODO
      break
  }
}

onMounted(() => {
  let notification: Notification = props.notification


  id.value = notification.id
  seen.value = notification.seen
})

</script>