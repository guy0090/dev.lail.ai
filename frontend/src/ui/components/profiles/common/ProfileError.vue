<template>
  <v-col>
    <v-card rounded border>
      <template v-slot:loader>
        <v-progress-linear class="bg-red" />
      </template>
      <v-card-text>
        <v-row class="pa-5" justify="center">
          <v-col cols="auto">
            <v-img src="/sprites/e404.webp" width="70" />
          </v-col>
          <v-col cols="auto" align-self="center">
            <h1 class="text-weight-medium">{{ $t(error) }}</h1>
          </v-col>
        </v-row>
      </v-card-text>
      <v-divider v-if="showActions" />
      <v-card-actions v-if="showActions">
        <v-btn color="green-darken-2" variant="elevated" @click="$router.push({ name: 'home' })">
          {{ $t('common.homeButtonLabel') }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-col>
</template>

<script setup lang="ts">
defineProps({
  error: {
    type: String,
    required: true
  },
  showActions: {
    type: Boolean,
    default: true
  }
})
</script>