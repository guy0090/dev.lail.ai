<template>
  <v-expansion-panel-text class="mt-1 pt-2">
    <player-skill v-for="(skill, i) in getSkills(participant.skills)" :key="i"
      :skill="skill" :player-damage="totalDamage(participant)" :class-id="participant.classId" />
  </v-expansion-panel-text>
</template>

<script setup lang="ts">
import PlayerSkill from './players/PlayerSkill.vue';
import type { EncounterEntity, EntitySkill } from '@/api/common/interfaces/Encounters';

import { currentEncounterStore } from '@/ui/store/Encounters';

defineProps({
  id: Number,
  participant: {
    type: Object as () => EncounterEntity,
    required: true,
  }
})

const currentEncounter = currentEncounterStore();

const getSkills = (skills: EntitySkill[]) => skills.sort((a, b) => b.damageDealt - a.damageDealt)

const totalDamage = (participant: EncounterEntity) => {
  return currentEncounter.getEntityDamage(participant);
}
</script>