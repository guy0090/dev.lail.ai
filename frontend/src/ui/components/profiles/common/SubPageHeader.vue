<template>
  <v-col :cols="cols" :lg="lg" align-self="center">
    <v-card border class="bg-grey-darken-4" width="fit-content">
      <v-card-text class="py-1 px-5">
        <span class="text-h6">
          <v-icon class="me-2" :icon="icon" />
          {{ title }}
        </span>
        <span class="text-grey" v-if="subtitle && width > thresholds.sm">
          &nbsp;{{ subtitle }}
        </span>
      </v-card-text>
    </v-card>
  </v-col>
</template>

<script setup lang="ts">
import { useDisplay } from 'vuetify'

const { thresholds, width } = useDisplay();

defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    required: false
  },
  icon: {
    type: String,
    required: true
  },
  lg: {
    type: String,
    default: '9'
  },
  cols: {
    type: String,
    default: 'auto'
  }
})
</script>