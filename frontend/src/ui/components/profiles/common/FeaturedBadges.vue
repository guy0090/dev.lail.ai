<template>
  <v-col cols="12" md="12" lg="6" xl="6">
    <v-card style="height:100%" class="pa-3 bg-grey-darken-3 elevation-n4">
      <v-row justify="center" style="height: 100%" class="mt-1">
        <v-col cols="auto" class="ma-auto" align-self="center">
          <v-row>
            <v-img src="/sprites/e400.webp" height="50" width="50" />
          </v-row>
        </v-col>
      </v-row>
    </v-card>
  </v-col>
</template>

<script setup lang="ts">
</script>

<style scoped>
.elevation-n1 {
  box-shadow:
    inset 0 2px 1px -1px rgba(0, 0, 0, 0.2),
    inset 0 1px 1px 0 rgba(0, 0, 0, 0.14),
    inset 0 1px 3px 0 rgba(0, 0, 0, 0.12) !important;
}

.elevation-n4 {
  box-shadow:
    inset 0px 2px 4px -1px var(--v-shadow-key-umbra-opacity, rgba(0, 0, 0, 0.2)),
    inset 0px 4px 5px 0px var(--v-shadow-key-penumbra-opacity, rgba(0, 0, 0, 0.14)),
    inset 0px 1px 10px 0px var(--v-shadow-key-penumbra-opacity, rgba(0, 0, 0, 0.12)) !important;
}

.elevation-n8 {
  box-shadow:
    inset 0px 5px 5px -3px var(--v-shadow-key-umbra-opacity, rgba(0, 0, 0, 0.2)),
    inset 0px 8px 10px 1px var(--v-shadow-key-penumbra-opacity, rgba(0, 0, 0, 0.14)),
    inset 0px 3px 14px 2px var(--v-shadow-key-penumbra-opacity, rgba(0, 0, 0, 0.12)) !important;
}

.elevation-n19 {
  box-shadow:
    inset 0px 9px 12px -6px var(--v-shadow-key-umbra-opacity, rgba(0, 0, 0, 0.2)),
    inset 0px 19px 29px 2px var(--v-shadow-key-penumbra-opacity, rgba(0, 0, 0, 0.14)),
    inset 0px 7px 36px 6px var(--v-shadow-key-penumbra-opacity, rgba(0, 0, 0, 0.12)) !important;
}
</style>