<template>
  <!-- TODO: Redo this to properly be responsive? (yes) -->
  <v-col v-if="width >= 800">
    <v-card link class="border encounter" @click="openEncounter(encounter)">
      <v-card-text class="py-3 ps-3 pe-3">
        <v-row>
          <v-col cols="auto" class="px-4 py-5 bg-grey-darken-3" align-self="center">
            <v-img width="50" height="50" :src="`/sprites/${getBestPlayer(encounter).classId}.png`" />
          </v-col>
          <v-col cols="auto" align-self="center">
            <span class="text-h4">{{
              getBestPlayer(encounter).name || $t(`game.classes.${getBestPlayer(encounter).classId}`)
            }}</span>
          </v-col>
          <v-col cols="auto" class="pa-1" align-self="center">
            <v-chip label :text="$t('common.abbreviatedDps', { dps: abbrNum(getBestPlayer(encounter).dps, 2) })"
              color="red" variant="elevated" size="default" prepend-icon="mdi-sword-cross" />
          </v-col>
          <v-spacer />
          <v-col cols="auto" class="py-0 px-0 border-s" align-self="center">
            <card-banner :height="90" :encounter="encounter"/>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-col>
  <encounter-card v-else :encounter="encounter" :best="getBestPlayer(encounter)" />
</template>

<script setup lang="ts">
import { useDisplay } from 'vuetify';
import { useRouter } from 'vue-router';
import CardBanner from './CardBanner.vue';
import EncounterCard from './EncounterCard.vue';

import { abbrNum } from '@/common/Utils';
import type { EncounterSummaryDto } from '@/api/common/dto/ResponseDtos';

defineProps({
  encounter: {
    type: Object as () => EncounterSummaryDto,
    required: true
  }
})

const { width } = useDisplay()
const router = useRouter();

const getBestPlayer = (encounter: EncounterSummaryDto) => {
  return [...encounter.participants].sort((a, b) => b.damage - a.damage)[0];
}

const openEncounter = (encounter: EncounterSummaryDto) => {
  router.push({
    name: 'log',
    params: {
      id: encounter.id
    }
  });
}
</script>