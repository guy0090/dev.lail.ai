<template>
  <v-row justify="center" class="flex-column">
    <v-col cols="auto">
      <v-card  elevation="23">
        <v-progress-linear absolute color="red" model-value="100" />
        <v-card-item>
          <v-row>
            <v-col cols="auto">
              <v-avatar size="80" image="/sprites/e400.webp" />
            </v-col>
            <v-col cols="auto" align-self="center">
              <h1 class="font-weight-medium">{{ $t('pages.login.error.message') }}</h1>
              <h3 class="font-weight-medium">{{
                exception instanceof ApiException
                  ? $t(`apiErrors.${exception.code ?? 0}`)
                  : exception.message
              }}</h3>
            </v-col>
          </v-row>
        </v-card-item>
        <v-divider />
        <v-card-actions>
          <v-btn class="px-4" variant="elevated" color="green" prepend-icon="mdi-arrow-left" @click="$router.push({ name: 'home' })">
            {{ $t('common.homeButtonLabel') }}
          </v-btn>
          <v-btn class="px-4" variant="elevated" color="indigo-accent-2" prepend-icon="mdi-discord" @click="toDiscordOAuth()">
            {{ $t('pages.login.error.actionRetry') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { toDiscordOAuth } from '@/api/http/Api';
import { ApiException } from '@/api/http/ApiException';
import { getApiException } from '@/common/Utils';
import { computed } from 'vue';

const props = defineProps<{
  error: Error
}>();

const exception = computed(() => getApiException(props.error));
</script>