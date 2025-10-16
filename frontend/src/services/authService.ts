import { apiRequest } from './api'

export interface User {
  username: string
  authenticated: boolean
}

export interface LoginRequest {
  username: string
  password: string
}

/**
 * Authentication service for handling login, logout, and user information.
 * Provides methods to interact with the authentication API endpoints.
 */
export class AuthService {
  /**
   * Login with username and password
   */
  async login(username: string, password: string): Promise<User> {
    const loginRequest: LoginRequest = { username, password }
    
    return await apiRequest<User>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(loginRequest)
    })
  }

  /**
   * Logout the current user
   */
  async logout(): Promise<void> {
    await apiRequest<void>('/api/auth/logout', {
      method: 'POST'
    })
  }

  /**
   * Get current authenticated user information
   */
  async getCurrentUser(): Promise<User> {
    return await apiRequest<User>('/api/auth/user', {
      method: 'GET'
    })
  }
}

export const authService = new AuthService()
