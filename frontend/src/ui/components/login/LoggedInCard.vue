<template>
  <v-row justify="center">
    <v-col cols="auto">
      <v-card class="px-3" elevation="23">
        <v-progress-linear absolute color="success" model-value="100" />
        <v-card-item>
          <v-row justify="center">
            <v-col cols="auto">
              <v-avatar size="80" :image="userState.getUserAvatar()" />
            </v-col>
            <v-col cols="auto" align-self="center">
              <h2>
                <v-icon class="check" color="green" icon="mdi-check" />
                {{ $t('pages.login.success.message', { user: getUsername() }) }}
              </h2>
            </v-col>
          </v-row>
        </v-card-item>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { userStore } from '@/ui/store/User';

const userState = userStore();

const getUsername = () => {
  const user = userState.getUser;
  const settings = userState.getSettings;

  if (user) {
    return settings.username || user.discordUsername+'#'+user.discriminator;
  }
  return "Unknown User";
}
</script>

<style scoped>
.check {
  padding-bottom: 4px
}
</style>