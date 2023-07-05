<template>
  <v-img :height="height" :width="width" :src="`/banners/${getZone(encounter)?.icon}.png`" cover
    class="text-white log-bg">
    <v-layout full-height>
      <v-col class="ps-4" align-self="center">
        <v-row justify="center">
          <v-avatar size="35" :image="`/zones/${getZone(encounter)?.type || 1}.webp`" />
          <h2 v-if="!getZone(encounter)?.gate" class="text-h5 me-2 align-self-center">{{ $t(`game.zones.${getZone(encounter)?.id}`) || "Unknown" }}</h2>
          <v-badge v-else :content="'G' + getZone(encounter)?.gate" color="red">
            <h2 class="text-h5 me-2 align-self-center">{{ $t(`game.zones.${getZone(encounter)?.id}`) || "Unknown" }}</h2>
          </v-badge>
          <!-- ? If we wanted multiple badges on title
              <v-badge content="Challenge" inline rounded="sm" color="red">
                <h2 class="text-h5 me-2">Chromanium</h2>
              </v-badge>
              <v-badge content="Wipe" inline rounded="sm" color="red"><h2 class="text-h5 mx-0 px-0">&#8203;</h2></v-badge>
              <v-badge content="Gate 1" inline rounded="sm" color="indigo"><h2 class="text-h5 mx-0 px-0">&#8203;</h2></v-badge>
              -->
        </v-row>
        <v-row justify="center">
          <v-col cols="auto" class="pa-1">
            <v-chip label :text="abbrNum(getTotalDps(encounter), 2) + '/s'" color="red" variant="elevated" size="small"
              prepend-icon="mdi-sword-cross"></v-chip>
          </v-col>
          <v-col cols="auto" class="pa-1">
            <v-chip label :text="getDuration(encounter.duration)" color="green" variant="elevated" size="small"
              prepend-icon="mdi-timer-sand" class="mx-1"></v-chip>
          </v-col>
          <v-col cols="auto" class="pa-1">
            <v-chip label :text="timeSince(encounter.created)" color="blue" variant="elevated" size="small"
              prepend-icon="mdi-cloud-upload"></v-chip>
          </v-col>
        </v-row>
      </v-col>
    </v-layout>
  </v-img>
</template>

<script setup lang="ts">
import type { EncounterSummaryDto } from '@/api/common/dto/ResponseDtos';
import { timeSince, abbrNum, getDuration } from '@/common/Utils';
import { ZONES } from '@/common/Zones';

defineProps({
  encounter: {
    type: Object as () => EncounterSummaryDto,
    required: true,
  },
  width: {
    type: Number,
    default: 370,
  },
  height: {
    type: Number,
    default: 125,
  }
});

const getZone = (encounter: EncounterSummaryDto) => {
  const zoneId = encounter.association.zone;
  return ZONES.get(zoneId);
}

const getTotalDps = (encounter: EncounterSummaryDto) => {
  return encounter.participants.reduce((acc, participant) => {
    return acc + participant.dps;
  }, 0);
}
</script>