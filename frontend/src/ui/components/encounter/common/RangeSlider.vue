<template>
  <v-row>
      <v-col cols="12">
        <v-range-slider
        v-model="range"
        :min="0"
        :max="currentEncounter.getEncounterDuration"
        :step="1"
        hide-details
        class="align-center"
        @update:model-value="changeRange"
      >
        <template v-slot:prepend>
          <v-text-field
            hide-details
            single-line
            type="number"
            variant="outlined"
            density="compact"
            style="width: 75px"
          >{{ getDuration(range[0]) }}</v-text-field>
        </template>
        <template v-slot:append>
          <v-text-field
            hide-details
            single-line
            type="number"
            variant="outlined"
            style="width: 75px"
            density="compact"
          >{{ getDuration(range[1]) }}</v-text-field>
        </template>
      </v-range-slider>
      </v-col>
    </v-row>
</template>

<script setup lang="ts">
import { getDuration } from '@/common/Utils';
import { ref } from 'vue';

import { currentEncounterStore } from '@/ui/store/Encounters';

const currentEncounter = currentEncounterStore();

const range = ref([0, currentEncounter.getEncounterDuration])

const changeRange = (value: number[]) => {
  const start = value[0] + currentEncounter.getEncounterStart
  const end = value[1] + currentEncounter.getEncounterStart

  currentEncounter.setRanges(start, end)
}
</script>