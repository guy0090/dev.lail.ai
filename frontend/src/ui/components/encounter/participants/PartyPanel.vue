<template>
  <v-row>
    <v-divider v-if="id === 2" class="mx-3 mt-2 mb-0" />
    <v-col :class="(id === 1 ? 'pt-1' : '') + ' pb-1'">
      <v-card class="border rounded">
        <v-card-title>
          <v-row justify="space-between">
            <v-col cols="auto" align-self="center">
              <v-icon>mdi-sword</v-icon>
              Party {{ id }}
              <small class="text-grey-darken-1">{{ partyId }}</small>
            </v-col>
            <v-col cols="auto" align-self="center" class="me-2">
              <v-row justify="end">
                <v-col cols="auto" class="py-0">
                  <span class="text-h6">{{ abbrNum(getPartyDps(participants), 1) }}/s</span>
                </v-col>
              </v-row>
              <v-row class="mt-0" justify="end">
                <v-col cols="auto" class="py-0">
                  <span class="text-body-1" style="letter-spacing: .8px;">
                    {{ abbrNum(getPartyDamage(participants), 2) }}
                    <small class="text-grey-darken-1" style="font-size: 92%">
                      {{ getDamagePercent(participants).toFixed(0) }}%
                    </small>
                  </span>
                </v-col>
              </v-row>
            </v-col>
          </v-row>
        </v-card-title>
      </v-card>
    </v-col>
    <participant-panel v-for="(player, i) in participants" :key="i" :id="i+1" :participant="player" />
  </v-row>
</template>

<script setup lang="ts">
import ParticipantPanel from './ParticipantPanel.vue';
import type { EncounterEntity } from '@/api/common/interfaces/Encounters';

import { abbrNum } from '@/common/Utils';
import { currentEncounterStore } from '@/ui/store/Encounters';

defineProps({
  id: {
    type: Number,
    required: true,
  },
  partyId: {
    type: String,
    required: true,
  },
  participants: {
    type: Array as () => EncounterEntity[],
    required: true,
  }
})

const currentEncounter = currentEncounterStore();

const getDamagePercent = (party: EncounterEntity[]) => {
  const total = currentEncounter.getTotalDamageDealt().total;
  const partyDamage = getPartyDamage(party);

  return (partyDamage / total) * 100;
}

const getPartyDamage = (party: EncounterEntity[]) => {
  return currentEncounter.getPartyDamage(party);
}

const getPartyDps = (party: EncounterEntity[]) => {
  return currentEncounter.getPartyDps(party);
}
</script>