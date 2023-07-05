<template>
  <footer class="flex mt-5 bg-grey-darken-4">
    <v-container fluid>
      <v-row>
        <v-spacer />
        <v-col cols="auto" class="pe-0">
          <v-img src="/icons/mokoko.png" :height="35" :width="35"
          @click="$router.push({ name: 'home' })" class="moko" contain />
        </v-col>
        <v-col cols="auto" class="ps-0">
          <v-btn variant="text" class="mx-1"><strong>About Us</strong></v-btn>
          <v-btn variant="text" class="mx-1"><strong>Privacy</strong></v-btn>
          <v-btn variant="text" class="mx-1"><strong>Rules &amp; FAQ</strong></v-btn>
        </v-col>
      </v-row>
    </v-container>
  </footer>
</template>

<style scoped>
.moko:hover {
  cursor: pointer;
}
</style>