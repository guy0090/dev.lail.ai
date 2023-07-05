import 'pinia'
import type { Router } from 'vue-router'


/**
 * Add typing to Pinia's custom properties for correct type checking on `$router`
 *
 * See:
 * - https://pinia.vuejs.org/core-concepts/plugins.html#adding-new-external-properties
 * - https://pinia.vuejs.org/core-concepts/plugins.html#typing-new-store-properties
 */
declare module 'pinia' {
  export interface PiniaCustomProperties {
    $router: Router // Add router "plugin" for use in any store
  }
}