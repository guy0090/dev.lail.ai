<template>
  <v-chip
    v-if="!role.hidden || showHidden"
    class="me-2"
    rounded="sm"
    :color="role.color || 'grey'"
    :prepend-icon="role.icon || ''"
    size="default"
  >
    {{ role.name }}
  </v-chip>
</template>

<script setup lang="ts">
import { BasicRoleDto } from '@/api/common/dto/ResponseDtos';


defineProps({
  role: {
    type: BasicRoleDto,
    required: true,
  },
  showHidden: {
    type: Boolean,
    required: false,
    default: false,
  },
})

</script>