<template>
  <v-container fluid>
    <v-spacer vertical />
    <v-row justify="center">
      <v-col cols="auto">
        <v-card border rounded>
          <template v-slot:loader>
            <v-progress-linear class="bg-red" />
          </template>
          <v-card-text>
            <v-row justify="center">
              <v-col cols="auto" align-self="center" class="pe-0">
                <v-img src="/icons/mokoko.png" width="50" height="50" />
              </v-col>
              <v-col cols="auto" align-self="center">
                <span class="text-h4">Work In Progress</span>
              </v-col>
            </v-row>
          </v-card-text>
          <v-divider />
          <v-card-actions>
            <v-btn variant="tonal" color="teal" @click="$router.push({ name: 'home' })">
              <v-icon icon="mdi-home" class="pe-1" />
              Home
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>