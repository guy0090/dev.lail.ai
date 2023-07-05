<template>
  <v-col cols="auto" class="pa-1">
    <v-chip :text="player.name && utilState.showEncounterPlayerNames ? player.name : $t(`game.classes.${player.classId}`)"
      label :color="getClassColor(player.classId)" class="ps-2" size="large">
      <template v-slot:prepend>
        <v-avatar rounded="0" class="pe-1" :image="`/sprites/${player.classId}.png`" />
      </template>
    </v-chip>
  </v-col>
</template>

<script setup lang="ts">
import type { EncounterParticipant } from '@/api/common/interfaces/Encounters';
import { getClassColor } from '@/common/Utils';
import { utilStore } from '@/ui/store/Util';

const utilState = utilStore();

defineProps({
  player: {
    type: Object as () => EncounterParticipant,
    required: true,
  }
});
</script>
