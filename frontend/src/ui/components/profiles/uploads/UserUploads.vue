<template>
  <v-row class="mt-6 mb-0" justify="center">
    <v-col cols="12" lg="11">
      <v-row justify="space-between">
        <sub-page-header
          :title="$t(`${I18N_KEY}.title`)"
          :subtitle="$t(`${I18N_KEY}.subtitle`, { hours: 3 })"
          icon="mdi-cloud-upload"
          lg=""
        />
        <v-col cols="auto" align-self="center">
          <v-btn color="green" class="me-2">
            <v-icon>mdi-reload</v-icon>
            <span class="ms-1" v-if="width > thresholds.md">{{ $t('pages.profile.uploads.refreshButtonLabel') }}</span>
          </v-btn>
          <v-btn-toggle v-model="panelToggle" variant="elevated" density="compact" border divided
            :disabled="uploads.length === 0" mandatory v-if="width >= 1100"
            @update:model-value="(e: number) => utilState.setEncounterOrientation(e)">
            <v-btn><v-icon>mdi-view-grid</v-icon></v-btn>
            <v-btn><v-icon>mdi-format-align-justify</v-icon></v-btn>
          </v-btn-toggle>
        </v-col>
      </v-row>
    </v-col>
  </v-row>
  <v-row class="mt-0" justify="center" v-if="panelToggle == 0 || width <= 1100">
    <v-col cols="12" md="11">
      <v-row justify="center">
        <encounter-card v-for="(encounter, i) in uploads" :key="i" :encounter="encounter" />
      </v-row>
    </v-col>
  </v-row>
  <v-row class="mt-0" v-else justify="center">
    <v-col cols="12" md="11">
      <v-row justify="center">
        <encounter-row v-for="(encounter, i) in uploads" :key="i" :encounter="encounter" />
      </v-row>
    </v-col>
  </v-row>
  <v-row v-if="uploads.length === 0" justify="center" class="mt-15">
    <no-data :intl="'pages.profile.uploads.noData'" />
  </v-row>
</template>

<script setup lang="ts">
import SubPageHeader from '../common/SubPageHeader.vue';
import EncounterCard from '@/ui/components/common/encounters/EncounterCard.vue';
import EncounterRow from '@/ui/components/common/encounters/EncounterRow.vue';
import NoData from '../common/NoData.vue';

import { useDisplay } from 'vuetify';
import { utilStore } from '@/ui/store/Util';
import { ref, computed } from 'vue';

const I18N_KEY = 'pages.profile.uploads'

const utilState = utilStore()

const uploads = computed(() => [] as any[])

const panelToggle = ref(utilState.getEncounterOrientation)
const { thresholds, width } = useDisplay();
</script>