import type { RouteRecordRaw } from "vue-router";
import Dashboard from "@/ui/components/admin/AdminDashboard.vue";

export default [
  {
    path: '',
    name: 'dashboard',
    component: Dashboard,
  }
] as RouteRecordRaw[]