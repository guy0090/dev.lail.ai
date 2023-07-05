<template>
    <v-col cols="12" class="pa-1">
      <v-card link border rounded color="grey-darken-4">
        <v-card-text class="py-2">
          <v-row>
            <v-col cols="auto">
              <v-avatar size="40" color="grey-darken-4">
                <v-img :src="getAvatar(member.user)" />
              </v-avatar>
            </v-col>
            <v-col cols="auto" align-self="center">
              <v-row>
                <span class="text-h6">{{ member.user.username }}</span>
              </v-row>
            </v-col>
            <v-spacer />
            <v-col cols="auto">
              <v-tooltip location="start" :text="$t(`game.classes.${member.participant.classId}`)">
                <template v-slot:activator="{ props }">
                  <v-avatar v-bind="props" rounded="0" class="pe-1" :image="`/sprites/${member.participant.classId}.png`" />
                </template>
              </v-tooltip>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-col>
</template>

<script setup lang="ts">
import type { ParticipantDto } from '@/api/common/dto/ResponseDtos';
import { getAvatar } from '@/common/Utils';

defineProps({
  member: {
    type: Object as () => ParticipantDto,
    required: true
  }
})
</script>