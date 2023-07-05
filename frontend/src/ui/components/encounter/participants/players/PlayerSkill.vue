<template>
  <v-row>
    <v-col cols="auto" align-self="center">
      <v-avatar size="45" class="skill-icon" rounded="0">
        <v-img v-on:error="onImgMissing" :src="getSkillIcon(skill.icon)" />
      </v-avatar>
    </v-col>
    <v-col cols="auto" align-self="center">
      <v-row>
        <span class="text-body-1 d-inline-block text-truncate">
          {{ $t(`game.data.${skill.id}`) }}
          <small class="text-grey-darken-1">{{ skill?.id }}</small>
        </span>
      </v-row>
      <v-row style="padding-top: 6px" class="mt-0">
        <span class="me-2 detail">Hits: {{ skill.breakdown.length }}</span>
        <span class="detail">HPM: {{ getHpm(skill) }}</span>
      </v-row>
      <v-row style="padding-top: 5px" class="mt-0">
        <span class="detail">
          Crits: <span>{{ skill.hits.crit }}</span>
          <span class="ms-1 detail-percent">{{ getCritRate(skill) }}%</span>
        </span>
        <span class="mx-2 detail">
          B. Hits: <span>{{ skill.hits.backAttack }}</span>
          <span class="ms-1 detail-percent">{{ getBackHitRate(skill) }}%</span>
        </span>
        <span class="detail">
          F. Hits: <span>{{ skill.hits.frontAttack }}</span>
          <span class="ms-1 detail-percent">{{ getFrontHitRate(skill) }}%</span>
        </span>
      </v-row>
    </v-col>
    <v-spacer />
    <v-col cols="auto" align-self="center" class="me-2">
      <v-row justify="end">
        <span class="text-body-1">{{ abbrNum(currentEncounter.getSkillDps(skill), 1) }}/s</span>
      </v-row>
      <v-row style="padding-top: 6px" class="mt-0" justify="end">
        <span class="detail">
          {{ abbrNum(currentEncounter.getSkillDamage(skill), 1) }}
          <span class="ms-0 detail-percent">{{ getDamagePercent(skill, playerDamage).toFixed(1) }}%</span>
        </span>
      </v-row>
      <v-row style="padding-top: 5px" class="mt-0" justify="end">
        <span class="detail">
          Max: {{ abbrNum(skill.maxDamage, 1) }}
        </span>
      </v-row>
    </v-col>
    <v-progress-linear class="mx-1" striped height="5" :model-value="getDamagePercent(skill, playerDamage)"
      :color="getClassColor(classId)" />
  </v-row>
</template>

<script setup lang="ts">
import type { EntitySkill } from '@/api/common/interfaces/Encounters';
import { getClassColor, abbrNum } from '@/common/Utils';
import { ref } from 'vue';

import { currentEncounterStore } from '@/ui/store/Encounters';

defineProps({
  skill: {
    type: Object as () => EntitySkill,
    required: true,
  },
  classId: {
    type: Number,
    default: 0,
  },
  playerDamage: {
    type: Number,
    default: 0,
  }
})

const currentEncounter = currentEncounterStore();
const missingImage = ref(false);

const getSkillIcon = (icon: string) => missingImage.value
  ? "/sprites/e400.webp"
  : `/sprites/${icon}`;

const onImgMissing = () => {
  missingImage.value = true;
}

const getDamagePercent = (skill: EntitySkill, total: number) => {
  return (skill.damageDealt / total * 100);
}

const getHpm = (skill: EntitySkill) => {
  const hits = skill.breakdown.length;
  const duration = currentEncounter.getDuration;

  return (hits / (duration / 1000 / 60)).toFixed(1);
}

const getCritRate = (skill: EntitySkill) => {
  const hits = skill.breakdown.length;
  const crits = skill.hits.crit;

  return (crits / hits * 100).toFixed(1);
}

const getFrontHitRate = (skill: EntitySkill) => {
  const hits = skill.breakdown.length;
  const frontHits = skill.hits.frontAttack;

  if (frontHits && hits) {
    const frontHitRate = (frontHits / hits) * 100;
    return frontHitRate.toFixed(1);
  } else {
    return "0";
  }
}

const getBackHitRate = (skill: EntitySkill) => {
  const hits = skill.breakdown.length;
  const backHits = skill.hits.backAttack;

  if (backHits && hits) {
    const backHitRate = (backHits / hits) * 100;
    return backHitRate.toFixed(1);
  } else {
    return "0";
  }
}
</script>

<style scoped>
.skill-icon {
  border-radius: 10% !important;
}

.detail {
  font-size: 11pt;
}

.detail-percent {
  color: grey;
  font-size: 9pt;
}
</style>
