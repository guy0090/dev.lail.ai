import type { App } from "vue"

export default (app: App) => {
  app.config.globalProperties.$window = window
}

declare module 'vue' {
  interface ComponentCustomProperties {
    $window: typeof window
  }
}