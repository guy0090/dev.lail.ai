<template>
  <v-col cols="12" class="pa-1" v-if="zone && summary">
    <v-card class="border encounter">
      <v-row>
        <v-col cols="auto" class="py-0 px-0" align-self="center">
          <v-img :height="height" :width="width" :src="`/banners/${zone.icon}.png`" cover
            class="text-white log-bg">
            <v-layout full-height>
              <v-col class="ps-4" align-self="center">
                <v-row justify="center">
                  <v-avatar size="38" :image="`/zones/${zone.type || 1}.webp`" />
                  <h2 v-if="!zone.gate" class="text-h4 me-2 align-self-center">{{
                    $t(`game.zones.${zone.id}`) || "Unknown" }}</h2>
                  <v-badge v-else :content="'G' +zone.gate" color="red">
                    <h2 class="text-h4 me-2 align-self-center">{{ $t(`game.zones.${zone.id}`) || "Unknown"
                    }}</h2>
                  </v-badge>
                </v-row>
                <v-row justify="center">
                  <v-col cols="auto" class="pa-1">
                    <v-chip label :text="getDuration(summary.duration)" color="green" variant="elevated" size="small"
                      prepend-icon="mdi-timer-sand" class="me-1"></v-chip>
                  </v-col>
                  <v-col cols="auto" class="pa-1">
                    <v-chip label :text="timeSince(summary.created)" color="blue" variant="elevated" size="small"
                      prepend-icon="mdi-cloud-upload"></v-chip>
                  </v-col>
                </v-row>
              </v-col>
            </v-layout>
          </v-img>
        </v-col>
      </v-row>
    </v-card>
  </v-col>
</template>

<script setup lang="ts">
import { getDuration, timeSince, getZone } from '@/common/Utils';
import { currentEncounterStore } from '@/ui/store/Encounters';
import { computed } from 'vue';

defineProps({
  width: {
    type: Number,
    default: 400,
  },
  height: {
    type: Number,
    default: 125,
  }
});

const currentEncounterState = currentEncounterStore();

const summary = computed(() => {
  const entry = currentEncounterState.getCurrent
  if (!entry) return undefined;
  return entry.summary;
})

const zone = computed(() => {
  const entry = currentEncounterState.getCurrent
  if (!entry) return undefined;
  return getZone(entry.summary);
})
</script>