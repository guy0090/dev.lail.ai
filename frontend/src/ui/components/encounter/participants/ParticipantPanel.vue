<template>
  <v-col cols="12" class="py-1">
    <v-expansion-panels>
      <v-expansion-panel class="border">
          <participant-panel-title :id="id" :participant="participant" />
          <participant-panel-content :id="id" :participant="participant" />
        </v-expansion-panel>
    </v-expansion-panels>
  </v-col>
</template>

<script setup lang="ts">
import ParticipantPanelTitle from './ParticipantPanelTitle.vue';
import ParticipantPanelContent from './ParticipantPanelContent.vue';
import type { EncounterEntity } from '@/api/common/interfaces/Encounters';

defineProps({
  id: Number,
  participant: {
    type: Object as () => EncounterEntity,
    required: true,
  }
})
</script>

<style scoped>
.gear-score {
  padding: 0px 8px !important;
}
</style>