<template>
  <Transition>
    <v-col cols="auto">
      <v-card class="border encounter" link hover max-width="370" height="100%" elevation="15" @click="openEncounter(encounter)">
        <card-banner :encounter="encounter" />

        <v-card-text class="py-2 px-6">
          <best-player-section v-if="best" :player="best" />
          <party-section v-else v-for="(party, i) in getParties(encounter)" :key="i" :party="party" />
        </v-card-text>
      </v-card>
    </v-col>
  </Transition>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import CardBanner from './CardBanner.vue';
import BestPlayerSection from './BestPlayerSection.vue';
import PartySection from './PartySection.vue';
import { getParties } from '@/common/Utils';
import type { EncounterSummaryDto } from '@/api/common/dto/ResponseDtos';
import type { EncounterParticipant } from '@/api/common/interfaces/Encounters';

defineProps({
  encounter: {
    type: Object as () => EncounterSummaryDto,
    required: true,
  },
  best: {
    type: Object as () => EncounterParticipant,
    required: false,
  },
  type: {
    type: String as () => '' | 'row' | 'card',
    required: false,
    default: 'card',
  }
});

const router = useRouter();

const openEncounter = (encounter: EncounterSummaryDto) => {
  router.push({
    name: 'log',
    params: {
      id: encounter.id
    }
  });
}
</script>

<style>
.log-bg>img {
  filter: opacity(0.6) !important;
}
</style>