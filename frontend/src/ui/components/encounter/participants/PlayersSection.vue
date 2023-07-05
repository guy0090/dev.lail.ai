<template>
  <!-- <v-row>
    <participant-panel v-for="(player, i) in players" :key="i" :id="i+1" :participant="player"/>
    <v-divider class="my-2 mx-3" v-if="esthers.length > 0" />
    <participant-panel v-for="(esther, i) in esthers" :key="i" :id="i+1" :participant="esther"/>
  </v-row> -->
  <party-panel v-for="([partyId, players], i) in parties" :key="i"
  :id="i+1" :party-id="partyId" :participants="players" />
</template>

<script setup lang="ts">
import PartyPanel from './PartyPanel.vue';

import { computed } from 'vue';
import { currentEncounterStore } from '@/ui/store/Encounters';

const currentEncounter = currentEncounterStore();

const parties = computed(() => {
  return currentEncounter.getParties();
});
</script>