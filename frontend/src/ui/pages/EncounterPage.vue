<template>
  <v-container fluid v-if="!data.loading && !data.error" class="px-0">
    <v-row>
      <encounter-summary />
      <encounter-participants />
    </v-row>
  </v-container>
  <v-container fluid v-else-if="data.loading">
    <v-row justify="center">
      <v-col cols="5">
        <v-card border>
          <template v-slot:loader>
            <v-progress-linear indeterminate color="teal" />
          </template>
          <v-card-title>Loading encounter '{{ $route.params.id }}'</v-card-title>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
  <v-container fluid v-else-if="data.error">
    <loading-failed :error="data.error" />
  </v-container>
</template>

<script setup lang="ts">
import EncounterSummary from '@/ui/components/encounter/EncounterSummary.vue';
import EncounterParticipants from '../components/encounter/EncounterParticipants.vue';
import LoadingFailed from '../components/encounter/common/LoadingFailed.vue';

import { reactive, ref, onMounted } from 'vue';
import { onBeforeRouteUpdate, useRoute } from 'vue-router';
import { isObjectId } from '@/common/Utils';
import { encounterStore, currentEncounterStore } from '../store/Encounters';
import type { ApiException } from '@/api/http/ApiException';

const route = useRoute();
const encounterState = encounterStore();
const currentEncounterState = currentEncounterStore();

const fakeLoadingMs = ref(100)
const data = reactive({
  loading: true,
  error: undefined as Error | ApiException | undefined,
})

const loadEncounter = async (id: string) => {
  data.loading = true;
  data.error = undefined;
  currentEncounterState.clearEncounter();

  encounterState.getEncounterEntry(id)
    .then((encounter) => {
      currentEncounterState.setEncounter(encounter);
      setTimeout(() => data.loading = false, fakeLoadingMs.value)
    })
    .catch((error) => {
      data.error = error;
      setTimeout(() => data.loading = false, fakeLoadingMs.value)
    })
}

onBeforeRouteUpdate((to) => {
  const currentId = route.params.id as string;
  const newId = to.params.id as string;

  if (currentId !== newId) {
    loadEncounter(newId)
  }
})

onMounted(() => {
  const encounterId = route.params.id as string;
  if (isObjectId(encounterId)) {
    loadEncounter(encounterId)
  } else {
    data.loading = false;
    data.error = new Error("Invalid encounter ID")
  }
})
</script>