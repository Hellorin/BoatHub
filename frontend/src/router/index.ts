import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Boats from '@/views/Boats.vue'
import { useAuthStore } from '@/stores/authStore'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: false }
  },
  {
    path: '/boats',
    name: 'Boats',
    component: Boats,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * Navigation guard to check authentication before accessing protected routes
 */
router.beforeEach(async (to, _, next) => {
  const authStore = useAuthStore()
  
  // Check if the route requires authentication
  if (to.meta.requiresAuth) {
    // Check if user is authenticated
    const isAuthenticated = await authStore.checkAuth()
    
    if (!isAuthenticated) {
      // Redirect to login page if not authenticated
      next('/')
    } else {
      next()
    }
  } else {
    // For public routes (like login), check if user is already authenticated
    if (to.path === '/' && authStore.isAuthenticated) {
      // If user is already logged in, redirect to boats page
      next('/boats')
    } else {
      next()
    }
  }
})

export default router

