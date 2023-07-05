<template>
  <v-row justify="center" class="my-1 py-2 bg-grey-darken-3 border rounded">
    <v-col cols="12" class="py-0">
      <strong class="text-grey">Top DPS</strong>
    </v-col>
    <v-col cols="auto" class="px-0" align-self="center">
      <v-img width="50" height="50" :src="`/sprites/${player.classId}.png`" />
    </v-col>
    <v-col cols="auto" align-self="center">
      <span class="text-h4">{{ $t(`game.classes.${player.classId}`) }}</span>
    </v-col>
    <v-col cols="auto" class="pa-1" align-self="center">
      <v-chip label :text="$t('common.abbreviatedDps', { dps: abbrNum(player.dps, 2) })"
      color="red" variant="elevated" size="large" prepend-icon="mdi-sword-cross" />
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import type { EncounterParticipant } from '@/api/common/interfaces/Encounters';
import { abbrNum } from '@/common/Utils';

defineProps({
  player: {
    type: Object as () => EncounterParticipant,
    required: true,
  }
});
</script>