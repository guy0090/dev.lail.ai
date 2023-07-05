<template>
  <v-row justify="center">
    <v-col cols="5">
      <v-card elevation="23">
        <v-progress-linear absolute color="red" model-value="100" />
        <v-card-item>
          <v-row>
            <v-col cols="auto">
              <v-avatar size="80" image="/sprites/e400.webp" />
            </v-col>
            <v-col cols="auto" align-self="center">
              <h1 class="font-weight-medium">{{ $t('pages.encounter.error') }}</h1>
              <h3 class="font-weight-medium">{{
                error instanceof ApiException
                ? $t(`apiErrors.${error.code ?? 0}`)
                : error.message
              }}</h3>
            </v-col>
          </v-row>
        </v-card-item>
        <v-divider />
        <v-card-actions>
          <v-btn class="px-4" variant="elevated" color="green" prepend-icon="mdi-arrow-left"
            @click="$router.push({ name: 'home' })">
            {{ $t('common.homeButtonLabel') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { ApiException } from '@/api/http/ApiException';

defineProps({
  error: {
    type: Error,
    required: true,
  },
})
</script>
