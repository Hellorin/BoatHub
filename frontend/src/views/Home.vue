<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>BoatHub</h1>
        <p>Please sign in to access your boat management platform</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">Username</label>
          <input
            id="username"
            v-model="username"
            type="text"
            required
            :disabled="loading"
            placeholder="Enter your username"
          />
        </div>
        
        <div class="form-group">
          <label for="password">Password</label>
          <input
            id="password"
            v-model="password"
            type="password"
            required
            :disabled="loading"
            placeholder="Enter your password"
          />
        </div>
        
        <div v-if="error" class="error-message">
          {{ error }}
        </div>
        
        <button 
          type="submit" 
          class="login-button"
          :disabled="loading || !username || !password"
        >
          <span v-if="loading">Signing in...</span>
          <span v-else>Sign In</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

/**
 * Handle login form submission
 */
const handleLogin = async () => {
  if (!username.value || !password.value) {
    error.value = 'Please enter both username and password'
    return
  }

  loading.value = true
  error.value = ''

  const success = await authStore.login(username.value, password.value)
  
  if (success) {
    // Redirect to boats page on successful login
    router.push('/boats')
  } else {
    error.value = authStore.error || 'Login failed. Please check your credentials.'
  }
  
  loading.value = false
}

/**
 * Check if user is already authenticated on component mount
 */
onMounted(async () => {
  const isAuthenticated = await authStore.checkAuth()
  if (isAuthenticated) {
    // User is already logged in, redirect to boats
    router.push('/boats')
  }
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.login-card {
  background: white;
  padding: 2.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border: 1px solid #e1e8ed;
  width: 100%;
  max-width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.login-header h1 {
  color: #2c3e50;
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
  font-weight: 700;
}

.login-header p {
  color: #7f8c8d;
  font-size: 1rem;
  margin: 0;
}

.login-form {
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-weight: 600;
  font-size: 0.9rem;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e1e8ed;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.form-group input:disabled {
  background-color: #f8f9fa;
  cursor: not-allowed;
}

.error-message {
  background-color: #fee;
  color: #c53030;
  padding: 0.75rem;
  border-radius: 6px;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  border: 1px solid #feb2b2;
}

.login-button {
  width: 100%;
  background-color: #667eea;
  color: white;
  padding: 0.75rem;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-button:hover:not(:disabled) {
  background-color: #5a67d8;
}

.login-button:disabled {
  background-color: #a0aec0;
  cursor: not-allowed;
}

.login-footer {
  text-align: center;
  padding-top: 1rem;
  border-top: 1px solid #e1e8ed;
}

.login-footer p {
  color: #7f8c8d;
  font-size: 0.85rem;
  margin: 0;
}
</style>

