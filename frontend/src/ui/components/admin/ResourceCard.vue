<template>
  <v-col sm="12" lg="4">
    <v-hover v-slot="{ isHovering, props }">
      <v-card
        class="pa-3" hover
        :elevation="isHovering ? 16 : 4"
        :color="isHovering ? 'grey-darken-3' : 'grey-darken-4'"
        v-bind="props"
        border="sm"
      >
        <v-row justify="space-between" class="pa-5">
          <v-col cols="auto" class="py-0">
            <v-row>
              <v-col cols="auto" align-self="center" class="ps-0">
                <v-icon :icon="icon" />
              </v-col>
              <v-col class="px-1" cols="auto">
                <h1>{{ amount }}</h1>
              </v-col>
            </v-row>
            <v-row class="my-0 py-0">
              <h3 class="font-weight-light">{{ title.toUpperCase() }}</h3>
            </v-row>
          </v-col>
          <v-col cols="auto"></v-col>
        </v-row>
        <v-row><v-divider class="my-0" /></v-row>
        <v-row class="px-5 py-2">
          <h3 class="text-indigo-accent-1">
            {{ subtitle }}
            <v-icon icon="mdi-chevron-right" />
          </h3>
        </v-row>
      </v-card>
    </v-hover>
  </v-col>
</template>

<script setup lang="ts">
defineProps<{
  icon: string
  amount: number
  title: string
  subtitle: string
  route: string
}>();
</script>