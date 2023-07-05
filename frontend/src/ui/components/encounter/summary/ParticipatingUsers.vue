<template>
  <v-col cols="12" class="pa-1">
    <v-expansion-panels>
      <v-expansion-panel class="border">
        <v-expansion-panel-title class="ps-4">
          <v-row>
            <v-col cols="auto" align-self="center">
              <v-avatar color="green"><v-icon>mdi-account-group</v-icon></v-avatar>
            </v-col>
            <v-col cols="auto" align-self="center" style="margin-bottom: 4px;">
              <v-row>
                <span class="text-h5">Participating Users</span>
              </v-row>
              <v-row class="mt-2">
                <span>Users that contributed to this encounter</span>
              </v-row>
            </v-col>
          </v-row>
        </v-expansion-panel-title>
        <v-expansion-panel-text class="pt-2">
          <template v-if="!data.loading && !data.error">
            <v-row>
              <participating-user-panel v-for="(user, i) in data.users" :key="i"  :member="user" />
            </v-row>
          </template>
          <template v-else-if="data.loading">
            <v-progress-linear indeterminate color="teal" />
          </template>
          <template v-else-if="data.error">
            {{ data.error.message }}
          </template>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-col>
</template>

<script setup lang="ts">
import { ParticipantDto } from '@/api/common/dto/ResponseDtos';
import type { EncounterParticipant } from '@/api/common/interfaces/Encounters';
import { onMounted, reactive } from 'vue';
import { getUsers } from '@/api/http/Api';
import type { ApiException } from '@/api/http/ApiException';
import ParticipatingUserPanel from './ParticipatingUserPanel.vue';

const props = defineProps({
  participants: {
    type: Object as () => EncounterParticipant[],
    required: true
  }
})

const data = reactive({
  users: [] as ParticipantDto[],
  loading: true,
  error: undefined as Error | ApiException | undefined
})

const loadUsers = (participants: EncounterParticipant[]) => {
  const ids = participants.map((p) => p.userId as string);

  getUsers(ids).then((response) => {
    const members = response.map((user) => {
      const p = participants.find((p) => p.userId === user.id);
      return new ParticipantDto(user, p!)
    })

    data.users = members;
    data.loading = false;
  }).catch((error) => {
    data.loading = false;
    data.error = error;
  })
}

onMounted(() => {
  const validUsers = props.participants
    .filter((p) => p.userId !== undefined)

  loadUsers(validUsers);
})
</script>