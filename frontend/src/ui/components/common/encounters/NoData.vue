<template>
  <v-col cols="12" md="6">
    <v-card class="border encounter" elevation="15">
      <v-progress-linear class="bg-red" />
      <v-card-text class="py-3 text-center">
        <v-row justify="center">
          <v-col align-self="center" cols="auto">
            <v-img src="/sprites/e400.webp" width="50" height="50" />
          </v-col>
          <v-col align-self="center" cols="auto">
            <span class="text-h5">No Data</span>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-col>
</template>

<script setup lang="ts">
</script>