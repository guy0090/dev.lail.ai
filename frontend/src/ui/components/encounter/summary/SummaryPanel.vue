<template>
  <v-col cols="6" class="pa-1">
    <v-card class="border encounter">
      <v-card-text>
        <v-row>
          <v-col cols="auto" align-self="center">
            <v-avatar :color="color">
              <v-icon>{{ icon }}</v-icon>
            </v-avatar>
          </v-col>
          <v-col align-self="center">
            <v-row>{{ title?.toUpperCase() }}</v-row>
            <v-row class="mt-1">
              <span class="text-h6">{{ subtitle }}</span>
            </v-row>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-col>
</template>

<script setup lang="ts">

defineProps({
  title: String,
  subtitle: String,
  color: String,
  icon: String,
})
</script>