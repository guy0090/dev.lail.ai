<template>
  <v-col cols="auto" style="max-width: 470px !important;">
    <v-row v-if="currentEncounter.getCurrent">
      <encounter-banner :height="140" :width="525" :encounter="currentEncounter.getSummary" />
      <summary-panel title="Damage Dealt" icon="mdi-sword-cross" color="red"
        :subtitle="Intl.NumberFormat().format(totalDamage.total)" />

      <summary-panel title="Party DPS" color="red" icon="mdi-fire"
        :subtitle="Intl.NumberFormat().format(totalDps.total)" />

      <summary-panel title="Avg. DPS" color="red" icon="mdi-fire"
        :subtitle="Intl.NumberFormat().format(totalDps.averagePlayer)" />

      <summary-panel title="Deaths" color="red" icon="mdi-skull"
        :subtitle="currentEncounter.getTotalDeaths().toFixed(0)" />

      <related-encounters v-if="related && related.length > 0" :encounters="related" />
      <!-- <participating-users v-if="userParticipants.length > 0" :participants="userParticipants" /> -->
    </v-row>
  </v-col>
</template>

<script setup lang="ts">
import EncounterBanner from '@/ui/components/encounter/summary/EncounterBanner.vue';
import SummaryPanel from '@/ui/components/encounter/summary/SummaryPanel.vue';
import RelatedEncounters from '@/ui/components/encounter/summary/RelatedEncounters.vue';
// import ParticipatingUsers from './summary/ParticipatingUsers.vue';
import { currentEncounterStore } from '@/ui/store/Encounters';
import { computed } from 'vue';

const currentEncounter = currentEncounterStore();

const totalDamage = computed(() => currentEncounter.getTotalDamageDealt())
const totalDps = computed(() => currentEncounter.getTotalDps())
const related = computed(() => currentEncounter.getRelated)
// const userParticipants = computed(() => currentEncounter.getUserParticipants())
</script>