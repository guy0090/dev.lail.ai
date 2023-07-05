<template>
  <v-row justify="center" class="my-1 py-2 bg-grey-darken-3 border rounded">
    <v-col cols="12" class="py-0">
      <strong class="text-grey">Party {{ party.id }}</strong>
    </v-col>
    <player-chip v-for="(player, index) in party.players" :key="index" :player="player" />
  </v-row>
</template>

<script setup lang="ts">
import type { EncounterParty } from '@/api/common/interfaces/Encounters';
import PlayerChip from './PlayerChip.vue';

defineProps({
  party: {
    type: Object as () => EncounterParty,
    required: true,
  },
});

</script>