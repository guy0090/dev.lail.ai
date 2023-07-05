<template>
  <v-col>
    <v-card rounded border>
      <template v-slot:loader>
        <v-progress-linear indeterminate color="teal" />
      </template>
      <v-card-text>
        <v-row class="pa-5" justify="center">
          <v-col cols="auto" align-self="center">
            <h1 class="text-weight-medium">{{ $t("pages.profile.loading.title", { name: profileId }) }}</h1>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-col>
</template>

<script setup lang="ts">
defineProps({
  profileId: {
    type: String,
    required: true
  }
})
</script>
