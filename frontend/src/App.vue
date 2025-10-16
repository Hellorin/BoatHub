<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-brand">
        <h1>BoatHub</h1>
      </div>
      <div class="nav-user" v-if="authStore.isAuthenticated">
        <span class="user-info">Welcome '{{ authStore.username }}' !</span>
        <button @click="handleLogout" class="logout-button" :disabled="authStore.loading">
          {{ authStore.loading ? 'Logging out...' : 'Logout' }}
        </button>
      </div>
      <div class="nav-auth" v-else>
        <router-link to="/" class="login-link">Login</router-link>
      </div>
    </nav>
    
    <main class="main-content" :class="{ 'login-page': $route.path === '/' }">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

/**
 * Check authentication status on app mount
 */
onMounted(async () => {
  await authStore.checkAuth()
})

/**
 * Handle user logout
 */
const handleLogout = async () => {
  await authStore.logout()
  router.push('/')
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #2c3e50;
  color: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.nav-brand h1 {
  margin: 0;
  font-size: 1.5rem;
}

.nav-links {
  display: flex;
  gap: 1rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-link.router-link-active {
  background-color: #3498db;
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-info {
  color: white;
  font-size: 0.9rem;
  font-weight: 500;
}

.logout-button {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logout-button:hover:not(:disabled) {
  background-color: #c0392b;
}

.logout-button:disabled {
  background-color: #a0aec0;
  cursor: not-allowed;
}

.nav-auth {
  display: flex;
  align-items: center;
}

.login-link {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
  font-size: 0.9rem;
  font-weight: 500;
}

.login-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.main-content {
  min-height: calc(100vh - 80px);
  padding: 2rem;
}

.main-content.login-page {
  padding: 0;
  min-height: calc(100vh - 80px);
}
</style>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
    'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  min-height: 100vh;
}
</style>

