<template>
  <v-col cols="auto" >
      <v-card class="bg-grey-darken-4" border>
        <v-progress-linear class="bg-teal" />
        <v-card-text class="py-1">
          <v-row justify="center">
            <v-col align-self="center" cols="auto" class="pe-1"><v-img src="/sprites/e404.webp" width="50" height="50" /></v-col>
            <v-col align-self="center" cols="auto"><span class="text-h6">{{ $t(intl) }}</span></v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-col>
</template>

<script setup lang="ts">
defineProps({
  intl: {
    type: String,
    required: true
  }
})
</script>