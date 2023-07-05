<template>
  <v-row>
    <v-col cols="12" class="pa-1">
      <v-card link border rounded color="grey-darken-4" @click="openEncounter(encounter)">
        <v-card-text>
          <v-row>
            <v-col cols="auto">
              <v-avatar size="55" color="grey-darken-4"><v-img cover
                  :src="`/banners/${getZone(encounter)?.icon}.png`" /></v-avatar>
            </v-col>
            <v-col cols="auto" align-self="center">
              <v-row>
                <v-badge inline :content="'G' + getZone(encounter)?.gate" color="red">
                  <span class="text-h5 me-1">{{ $t(`game.zones.${getZone(encounter)?.id}`) || "Unknown" }}</span>
                </v-badge>
              </v-row>
              <v-row class="mt-2">
                <v-col cols="auto" class="pa-1">
                  <v-chip label :text="abbrNum(getTotalDpsInSummary(encounter), 2) + '/s'" color="red" variant="elevated" size="small"
                    prepend-icon="mdi-sword-cross"></v-chip>
                </v-col>
                <v-col cols="auto" class="py-1 px-0">
                  <v-chip label :text="getDuration(encounter.duration)" color="green" variant="elevated" size="small"
                    prepend-icon="mdi-timer-sand"></v-chip>
                </v-col>
                <v-col cols="auto" class="pa-1">
                  <v-chip label :text="timeSince(encounter.created)" color="blue" variant="elevated" size="small"
                    prepend-icon="mdi-cloud-upload"></v-chip>
                </v-col>
              </v-row>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import type { EncounterSummaryDto } from '@/api/common/dto/ResponseDtos';
import { abbrNum, getDuration, timeSince, getZone, getTotalDpsInSummary } from '@/common/Utils';

defineProps({
  encounter: {
    type: Object as () => EncounterSummaryDto,
    required: true
  }
})

const router = useRouter();
const openEncounter = (encounter: EncounterSummaryDto) => {
  router.push({
    name: 'log',
    params: {
      id: encounter.id,
    },
  });
}
</script>