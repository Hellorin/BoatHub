import { defineStore } from 'pinia'
import { authService } from '../services/authService'
import { csrfService } from '../services/csrfService'

export interface User {
  username: string
  authenticated: boolean
}

export interface AuthState {
  user: User | null
  isAuthenticated: boolean
  loading: boolean
  error: string | null
}

/**
 * Authentication store using Pinia for state management.
 * Handles user authentication state, login, logout, and session management.
 */
export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    user: null,
    isAuthenticated: false,
    loading: false,
    error: null
  }),

  getters: {
    /**
     * Get the current username if authenticated
     */
    username: (state): string | null => {
      return state.user?.username || null
    }
  },

  actions: {
    /**
     * Login with username and password
     */
    async login(username: string, password: string): Promise<boolean> {
      this.loading = true
      this.error = null

      try {
        const user = await authService.login(username, password)
        this.user = user
        this.isAuthenticated = true
        this.loading = false
        
        // Refresh CSRF token after successful login to ensure we have a valid token for the new session
        await csrfService.refreshToken()
        
        return true
      } catch (error: any) {
        this.error = error.message || 'Login failed'
        this.loading = false
        return false
      }
    },

    /**
     * Logout the current user
     */
    async logout(): Promise<void> {
      this.loading = true
      this.error = null

      try {
        await authService.logout()
        this.user = null
        this.isAuthenticated = false
        this.loading = false
      } catch (error: any) {
        this.error = error.message || 'Logout failed'
        this.loading = false
        // Even if logout fails on server, clear local state
        this.user = null
        this.isAuthenticated = false
      } finally {
        // Always clear CSRF token on logout to prevent stale token issues
        csrfService.clearToken()
      }
    },

    /**
     * Check if user is currently authenticated
     */
    async checkAuth(): Promise<boolean> {
      this.loading = true
      this.error = null

      try {
        const user = await authService.getCurrentUser()
        this.user = user
        this.isAuthenticated = true
        this.loading = false
        return true
      } catch (error: any) {
        this.user = null
        this.isAuthenticated = false
        this.loading = false
        return false
      }
    },

    /**
     * Clear authentication state and error
     */
    clearError(): void {
      this.error = null
    },

    /**
     * Clear all authentication state including CSRF token
     */
    clearAuthState(): void {
      this.user = null
      this.isAuthenticated = false
      this.loading = false
      this.error = null
      csrfService.clearToken()
    }
  }
})
