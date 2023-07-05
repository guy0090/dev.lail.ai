<template>
  <v-progress-linear striped :model-value="getDamagePercent(participant)" :color="getClassColor(participant.classId)"
    height="7px" />
  <v-expansion-panel-title>
    <v-row>
      <v-col cols="auto" class="py-1 ps-0">
        <v-avatar v-if="participant.isPlayer" size="52" rounded="0" :image="`/sprites/${participant.classId}.png`" />
        <v-avatar v-else-if="participant.isEsther" size="52" :image="`/sprites/${participant.icon}`" />
      </v-col>
      <v-col cols="auto" class="py-1 ps-1" align-self="center">
        <v-row>
          <span class="text-h6">

            <small class="text-grey-darken-1">{{ participant.isPlayer ? id : participant.npcId }}</small>
            <v-tooltip location="end" text="Floormat">
              <template v-slot:activator="{ props }">
                <span v-bind="props" v-if="participant.isDead" class="ms-1">💀</span>
              </template>
            </v-tooltip>
            <span class="ms-1">
            {{
              participant.isEsther
                ? $t(`game.esthers.${getEstherId(participant)}`)
                : $t(`game.classes.${participant.classId}`)
            }}
            </span>
            <v-chip v-if="participant.isPlayer" class="bg-red gear-score ms-2" density="compact" rounded="sm">{{
              participant.gearScore }}</v-chip>
          </span>
        </v-row>
      </v-col>
      <v-spacer />
      <v-col cols="auto" align-self="center" class="me-5">
        <v-row class="pb-0" justify="end">
          <span class="text-h6">{{ abbrNum(getDps(participant), 1) }}/s</span>
        </v-row>
        <v-row class="pt-0 mt-1">
          <span class="text-body-2" style="letter-spacing: .8px;">
            {{ abbrNum(getDamageDealt(participant), 2) }}
            <small class="text-grey-darken-1" style="font-size: 92%">
              {{ getDamagePercent(participant).toFixed(0) }}%
            </small>
          </span>
        </v-row>
      </v-col>
    </v-row>
  </v-expansion-panel-title>
</template>

<script setup lang="ts">
import type { EncounterEntity } from '@/api/common/interfaces/Encounters';
import { abbrNum, getClassColor } from '@/common/Utils';

import { currentEncounterStore } from '@/ui/store/Encounters';

defineProps({
  id: Number,
  participant: {
    type: Object as () => EncounterEntity,
    required: true,
  }
})

const currentEncounter = currentEncounterStore();

const getDamageDealt = (entity: EncounterEntity) => {
  return currentEncounter.getEntityDamage(entity);
}

const getDps = (entity: EncounterEntity) => {
  return currentEncounter.getEntityDps(entity);
}

const getDamagePercent = (entity: EncounterEntity) => {
  const totalDamage = currentEncounter.getCurrent?.encounter.damageStatistics.totalDamageDealt ?? 0;
  const entityDamage = currentEncounter.getEntityDamage(entity);

  return (entityDamage / totalDamage) * 100;
}

const getEstherId = (entity: EncounterEntity) => {
  return entity.icon.split('_')[1].split('.')[0];
}
</script>

<style scoped>
.gear-score {
  padding: 0px 6px !important;
}
</style>
