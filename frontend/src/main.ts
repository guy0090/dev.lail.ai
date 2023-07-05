import { createApp, markRaw } from 'vue'
import { createI18n } from 'vue-i18n'
import { createPinia } from 'pinia'
import VueGtag from "vue-gtag";
import { socketStore } from '@/ui/store/WebSocket'
import { userStore } from '@/ui/store/User'
import { login } from '@/api/http/Api'

import messages from '@intlify/unplugin-vue-i18n/messages'
import App from '@/ui/App.vue'
import router from '@/ui/router/Router'

import VueGlobalProperties from '@/ui/config/VueGlobalProperties'

// ! Adding local Vuetify3 resources here since I was having CDN issues.
// Local Roboto
import '@fontsource/roboto'
// Local Material Design Icons
import '@mdi/font/css/materialdesignicons.css'
// ! --------------------------------------------------------------------

import 'vuetify/styles'

import { createVuetify } from 'vuetify'

const vuetify = createVuetify({
  theme: {
    defaultTheme: 'dark'
  }
})

const app = createApp(App)
VueGlobalProperties(app)

const i18n = createI18n({
  locale: 'en',
  messages
})

const pinia = createPinia()
pinia.use(({ store }) => {
  store.$router = markRaw(router)
})

app.use(i18n)
app.use(pinia)
app.use(router)
app.use(vuetify)

const isProd = import.meta.env.PROD
if (isProd) {
  const measurementId = import.meta.env.VITE_GOOGLE_ANALYTICS_ID
  if (!measurementId) {
    console.warn('Missing VITE_GOOGLE_ANALYTICS_ID')
  } else {
    const gtagConfig = { config: { id: measurementId } }
    app.use(VueGtag, gtagConfig, router)
  }
}

const socketState = socketStore()
const userState = userStore()

if (userState.user) {
  login().then((details) => {
    userState.updateInfo(details)
    socketState.reconnect()
  }).catch((err) => {
    console.error("Error with preauth: ", err)
    userState.logout()
    socketState.reconnect()
  })
}

app.mount('#app')
