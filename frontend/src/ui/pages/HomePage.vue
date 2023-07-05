<template>
  <v-row class="mb-0 mt-1">
    <v-col class="py-0" cols="auto" align-self="center">
      <v-card class="bg-transparent" elevation="0">
        <v-card-text class="py-2">
          <v-row>
            <v-col cols="auto" align-self="center">
              <strong class="text-h5">
                <v-icon icon="mdi-medal" class="me-3" />{{ $t('pages.home.topDps') }}&nbsp;
              </strong>
              <span class="text-grey">{{ $t('pages.home.topDpsSubtitle') }}</span>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
  <v-row class="mt-0 mb-2" justify="center">
    <top-dps-card v-if="encounterState.topEncounter && !loadingTop" :encounter="encounterState.topEncounter" />
    <loading-data v-else-if="loadingTop" />
    <no-data v-else />
  </v-row>

  <v-divider />
  <v-row class="mt-2">
    <v-col cols="auto" class="py-0" align-self="center">
      <v-card class="bg-transparent" elevation="0">
        <v-card-text class="py-2">
          <v-row justify="space-between">
            <v-col cols="auto" align-self="center">
              <strong class="text-h5">
                <v-icon icon="mdi-cloud-upload" class="me-3" />{{ $t('pages.home.recentUploads') }}&nbsp;
              </strong>
              <span class="text-grey">{{ $t('pages.home.recentUploadsSubtitle', { duration: 3 }) }}</span>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-col>
    <v-spacer />
    <v-col cols="auto" align-self="center" class="px-0" v-if="userState.hasPermission('encounters.manage.view')">
      <v-btn-toggle variant="elevated" density="compact" border divided>
        <v-btn @click="togglePlayerNames()" :active="utilState.showEncounterPlayerNames">
          <v-icon>mdi-account-eye</v-icon>
        </v-btn>
      </v-btn-toggle>
    </v-col>
    <v-col cols="auto" align-self="center">
      <v-btn-toggle v-model="panelToggle" variant="elevated" density="compact" border divided mandatory
        v-if="width > 1100" @update:model-value="(e: number) => utilState.setEncounterOrientation(e)"
        :disabled="encounterState.recentEncounters.length === 0"
      >
        <v-btn><v-icon>mdi-view-grid</v-icon></v-btn>
        <v-btn><v-icon>mdi-format-align-justify</v-icon></v-btn>
      </v-btn-toggle>
    </v-col>
  </v-row>
  <v-row justify="space-evenly" v-if="panelToggle == 0 || width <= 1100">
    <template v-if="encounterState.recentEncounters.length > 0 && !loadingRecents">
      <encounter-card v-for="(encounter, i) in encounterState.recentEncounters" :encounter="encounter" :key="i" />
    </template>
    <loading-data v-else-if="loadingRecents" />
    <no-data v-else />
  </v-row>
  <v-row justify="center" v-else>
    <template v-if="encounterState.recentEncounters.length > 0 && !loadingRecents">
      <encounter-row v-for="(encounter, i) in encounterState.recentEncounters" :encounter="encounter" :key="i" />
    </template>
    <loading-data v-else-if="loadingRecents" />
    <no-data v-else />
  </v-row>
</template>

<script setup lang="ts">
import { useDisplay } from 'vuetify';
import { ref } from 'vue'
import TopDpsCard from '@/ui/components/common/encounters/TopDpsCard.vue';
import EncounterCard from '@/ui/components/common/encounters/EncounterCard.vue';
import EncounterRow from '../components/common/encounters/EncounterRow.vue';
import LoadingData from '@/ui/components/common/encounters/LoadingData.vue';
import NoData from '@/ui/components/common/encounters/NoData.vue';
import { utilStore } from '@/ui/store/Util';
import { encounterStore } from '@/ui/store/Encounters';
import { userStore } from '@/ui/store/User';
import { getTopAndRecentEncounters } from '@/api/http/Api';
import { onMounted } from 'vue';

const { width } = useDisplay();
const utilState = utilStore()
const encounterState = encounterStore()
const userState = userStore()

const panelToggle = ref(utilState.getEncounterOrientation)

const loadingTop = ref(true)
const loadingRecents = ref(true)

const togglePlayerNames = () => {
  if (loadingRecents.value || loadingTop.value) return
  utilState.toggleShowEncounterPlayerNames()
  loadTopAndRecents(500)
}

const loadTopAndRecents = (timeout = 0) => {
  loadingTop.value = true
  loadingRecents.value = true
  getTopAndRecentEncounters(utilState.showEncounterPlayerNames).then((data) => {
    encounterState.setTopEncounter(data.top)
    encounterState.setRecentEncounters(data.recents)
    setTimeout(() => {
      loadingTop.value = false
      loadingRecents.value = false
    }, timeout)
  })
  .catch((err) => {
    console.error("Error loading top and recent encounters", err)
    setTimeout(() => {
      loadingTop.value = false
      loadingRecents.value = false
    }, timeout)
  })
}

onMounted(() => {
  if (encounterState.recentEncounters.length === 0) {
    loadTopAndRecents()
  } else {
    loadingTop.value = false
    loadingRecents.value = false
  }
})
</script>

<style>
.wireframe {
  opacity: 1;
  border-style: dashed;
}

.encounter>* {
  user-select: none !important;
}
</style>