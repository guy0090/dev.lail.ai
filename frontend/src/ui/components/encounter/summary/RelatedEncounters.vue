<template>
  <v-col cols="12" class="pa-1">
    <v-expansion-panels>
      <v-expansion-panel class="border">
        <v-expansion-panel-title class="ps-4">
          <v-row>
            <v-col cols="auto" align-self="center">
              <v-avatar color="green"><v-icon>mdi-link</v-icon></v-avatar>
            </v-col>
            <v-col cols="auto" align-self="center" style="margin-bottom: 4px;">
              <v-row>
                <span class="text-h5">Related Encounters</span>
              </v-row>
              <v-row class="mt-2">
                <span>Encounters from the same raid</span>
              </v-row>
            </v-col>
          </v-row>
        </v-expansion-panel-title>
        <v-expansion-panel-text class="pt-2">
          <related-encounter-panel v-for="encounter in encounters" :encounter="encounter" :key="encounter.id" />
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-col>
</template>

<script setup lang="ts">
import type { EncounterSummaryDto } from '@/api/common/dto/ResponseDtos';
import RelatedEncounterPanel from './RelatedEncounterPanel.vue';

defineProps({
  encounters: {
    type: Object as () => EncounterSummaryDto[],
    required: true
  }
})
</script>