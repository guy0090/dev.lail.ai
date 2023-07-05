import type { RouteRecordRaw } from 'vue-router'

import RecentUserUploads from '@/ui/components/profiles/uploads/UserUploads.vue'
import UserFollowing from '@/ui/components/profiles/following/UserFollowing.vue'
import UserBadges from '@/ui/components/profiles/badges/UserBadges.vue'
import UserSettings from '@/ui/components/profiles/settings/UserSettings.vue'

export default [
  {
    path: '',
    name: 'userUploads', // User recent uploads is default view
    component: RecentUserUploads,
  },
  {
    path: 'badges',
    name: 'userBadges',
    component: UserBadges,
  },
  {
    path: 'following',
    name: 'userFollowing',
    component: UserFollowing,
  },
  {
    path: 'settings',
    name: 'userSettings',
    component: UserSettings
  }
] as RouteRecordRaw[]
