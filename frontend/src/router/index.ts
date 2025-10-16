import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Boats from '@/views/Boats.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/boats',
    name: 'Boats',
    component: Boats
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

