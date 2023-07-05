import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '@/ui/views/HomeView.vue'
import AdminView from '@/ui/views/AdminView.vue'
import LoginView from '@/ui/views/LoginView.vue'
import ProfileView from '@/ui/views/ProfileView.vue'
import ProfileRoutes from '@/ui/router/children/ProfileRoutes'
import RankingsView from '@/ui/views/RankingsView.vue'
import StatisticsView from '@/ui/views/StatisticsView.vue'
import EncounterView from '@/ui/views/EncounterView.vue'

import * as NavGuard from './NavGuard'
import AdminRoutes from './children/AdminRoutes'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/admin',
      component: AdminView,
      meta: { permission: 'admin' },
      children: AdminRoutes
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: "/@/:id",
      component: ProfileView,
      children: ProfileRoutes,
      name: 'profile'
    },
    {
      path: "/rankings",
      name: 'rankings',
      component: RankingsView
    },
    {
      path: "/stats",
      name: 'statistics',
      component: StatisticsView
    },
    {
      path: "/log/:id",
      name: 'log',
      component: EncounterView
    },
    // Redirects on invalid routes
    // See https://router.vuejs.org/guide/migration/#removed-star-or-catch-all-routes
    { path: '/:pathMatch(.*)*', name: 'not-found', redirect: '/' },
  ]
})

NavGuard.configure(router)

export default router
