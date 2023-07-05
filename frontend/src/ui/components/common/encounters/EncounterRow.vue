<template>
  <v-col cols="12">
    <v-card link class="border encounter" @click="openEncounter(encounter)">
      <v-card-text class="py-3 ps-3 pe-3">
        <v-row>
          <v-col cols="auto" class="py-0 px-0" align-self="center">
            <card-banner :height="180" :encounter="encounter" />
          </v-col>
          <v-spacer />
          <v-col cols="auto" class="ps-5 py-0" align-self="center" >
            <party-section v-for="(party, i) in getParties(encounter)" :key="i" :party="party" />
          </v-col>
          <v-spacer />
        </v-row>
      </v-card-text>
    </v-card>
  </v-col>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import type { EncounterSummaryDto } from '@/api/common/dto/ResponseDtos';
import CardBanner from './CardBanner.vue';
import PartySection from './PartySection.vue';
import { getParties } from '@/common/Utils';

defineProps({
  encounter: {
    type: Object as () => EncounterSummaryDto,
    required: true,
  },
  cols: {
    type: Number,
    default: 8,
  },
})

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