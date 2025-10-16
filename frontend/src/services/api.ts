/**
 * Base API configuration and utilities
 */

import { csrfService } from './csrfService'

const API_BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:8080'

export interface ApiConfig {
  baseURL: string
  timeout: number
  headers: Record<string, string>
}

export const defaultApiConfig: ApiConfig = {
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
}

export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
    public response?: Response
  ) {
    super(message)
    this.name = 'ApiError'
  }
}

// CSRF token handling is now managed by csrfService

/**
 * Generic API request function with error handling
 */
export async function apiRequest<T>(
  endpoint: string,
  options: RequestInit = {},
  config: Partial<ApiConfig> = {}
): Promise<T> {
  const finalConfig = { ...defaultApiConfig, ...config }
  const url = `${finalConfig.baseURL}${endpoint}`
  
  // Get headers with CSRF token if needed
  const csrfHeaders = await csrfService.getHeadersForRequest(
    options.method || 'GET',
    url
  )
  
  const headers: Record<string, string> = {
    ...finalConfig.headers,
    ...csrfHeaders,
    ...(options.headers as Record<string, string>)
  }
  
  const requestOptions: RequestInit = {
    ...options,
    credentials: 'include', // Include cookies for session-based authentication
    headers
  }

  try {
    const response = await fetch(url, requestOptions)
    
    if (!response.ok) {
      let errorMessage = `HTTP ${response.status}: ${response.statusText}`
      
      // Handle 401 Unauthorized with specific message for login
      if (response.status === 401) {
        // Only redirect if not already on login page to avoid infinite loops
        if (window.location.pathname !== '/' && window.location.pathname !== '/login') {
          window.location.href = '/'
        }
        // Show user-friendly message for 401 errors
        errorMessage = 'Invalid username or password'
      } else if (response.status === 403) {
        // Handle 403 Forbidden - try CSRF token refresh
        try {
          const errorData = await response.json()
          if (errorData.message?.includes('CSRF')) {
            // Try to refresh CSRF token and retry the request
            await csrfService.refreshToken()
            const retryHeaders = await csrfService.getHeadersForRequest(
              options.method || 'GET',
              url
            )
            
            const retryOptions: RequestInit = {
              ...options,
              credentials: 'include',
              headers: {
                ...finalConfig.headers,
                ...retryHeaders,
                ...(options.headers as Record<string, string>)
              }
            }
            
            const retryResponse = await fetch(url, retryOptions)
            if (retryResponse.ok) {
              return await retryResponse.json()
            }
          }
        } catch (retryError) {
          console.error('Failed to retry request after CSRF refresh:', retryError)
        }
        
        errorMessage = 'Access forbidden. Please refresh the page and try again.'
        console.error('403 Forbidden - Possible CSRF token issue for:', options.method, endpoint)
      } else {
        try {
          const errorData = await response.json()
          errorMessage = errorData.message || errorMessage
        } catch {
          // If response is not JSON, use the default error message
        }
      }
      
      throw new ApiError(errorMessage, response.status, response)
    }
    
    // Handle empty responses (like 204 No Content)
    if (response.status === 204 || response.headers.get('content-length') === '0') {
      return {} as T
    }
    
    return await response.json()
  } catch (error) {
    if (error instanceof ApiError) {
      throw error
    }
    
    // Network or other errors
    throw new ApiError(
      error instanceof Error ? error.message : 'Network error occurred',
      0
    )
  }
}

/**
 * Build query string from parameters
 */
export function buildQueryString(params: Record<string, any>): string {
  const searchParams = new URLSearchParams()
  
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      searchParams.append(key, String(value))
    }
  })
  
  const queryString = searchParams.toString()
  return queryString ? `?${queryString}` : ''
}
