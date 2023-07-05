<template>
  <v-col cols="12" md="6">
    <v-card class="border encounter" elevation="15" loading>
      <template v-slot:loader>
        <v-progress-linear color="teal" active indeterminate height="5"/>
      </template>
      <v-card-text class="py-5 text-center">
        <span class="text-h5">Loading Data...</span>
      </v-card-text>
    </v-card>
  </v-col>
</template>

<script setup lang="ts">
</script>