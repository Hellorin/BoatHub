/**
 * CSRF service for managing CSRF tokens
 */
class CsrfService {
  private token: string | null = null
  private headerName: string = 'X-CSRF-TOKEN'
  private parameterName: string = '_csrf'
  private isInitialized: boolean = false
  private readonly API_BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:8080'

  /**
   * Initializes the CSRF service by fetching the token
   * 
   * @returns {Promise<void>}
   */
  async initialize(): Promise<void> {
    if (this.isInitialized) {
      return
    }

    try {
      await this.fetchToken()
      this.isInitialized = true
    } catch (error) {
      console.error('Failed to initialize CSRF service:', error)
      // Don't throw error to prevent app from breaking if CSRF setup fails
    }
  }

  /**
   * Fetches the CSRF token from the backend
   * 
   * @returns {Promise<void>}
   */
  async fetchToken(): Promise<void> {
    try {
      const response = await fetch(`${this.API_BASE_URL}/api/csrf-token`, {
        method: 'GET',
        credentials: 'include'
      })
      
      if (response.ok) {
        const data = await response.json()
        this.token = data.token
        this.headerName = data.headerName
        this.parameterName = data.parameterName
      } else {
        throw new Error(`Failed to fetch CSRF token: ${response.status}`)
      }
    } catch (error) {
      console.error('Error fetching CSRF token:', error)
      throw error
    }
  }

  /**
   * Gets the current CSRF token, fetching it if necessary
   * 
   * @returns {Promise<string|null>} The CSRF token
   */
  async getToken(): Promise<string | null> {
    if (!this.token) {
      await this.fetchToken()
    }
    return this.token
  }

  /**
   * Gets headers for a request that includes the CSRF token
   * 
   * @param method The HTTP method
   * @param url The request URL
   * @returns {Promise<Record<string, string>>} Headers with CSRF token
   */
  async getHeadersForRequest(method: string, url: string): Promise<Record<string, string>> {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }

    // Only add CSRF token for authenticated endpoints (not public ones)
    const isPublicEndpoint = url.includes('/api/csrf-token')
    
    if (!isPublicEndpoint && (method === 'POST' || method === 'PUT' || 
        method === 'DELETE' || method === 'PATCH')) {
      
      // Ensure we have a token
      if (!this.token) {
        await this.fetchToken()
      }
      
      if (this.token) {
        headers[this.headerName] = this.token
      }
    }
    
    return headers
  }

  /**
   * Handles CSRF token errors and retries the request
   * 
   * @param error The error response
   * @param originalRequest The original request function
   * @returns {Promise<any>} The retry result or rejected promise
   */
  async handleCsrfError(error: any, originalRequest: () => Promise<any>): Promise<any> {
    if (error.status === 403 && 
        error.message?.includes('CSRF')) {
      
      try {
        // Clear the old token and fetch a new one
        this.token = null
        await this.fetchToken()
        
        // Retry the original request
        return await originalRequest()
      } catch (refreshError) {
        console.error('Failed to refresh CSRF token:', refreshError)
        throw error
      }
    }
    
    throw error
  }

  /**
   * Refreshes the CSRF token
   * 
   * @returns {Promise<void>}
   */
  async refreshToken(): Promise<void> {
    this.token = null
    await this.fetchToken()
  }

  /**
   * Clears the stored CSRF token
   */
  clearToken(): void {
    this.token = null
  }

  /**
   * Gets the current token without making a request
   * 
   * @returns {string|null} The current token
   */
  getCurrentToken(): string | null {
    return this.token
  }

  /**
   * Gets the header name for CSRF token
   * 
   * @returns {string} The header name
   */
  getHeaderName(): string {
    return this.headerName
  }

  /**
   * Gets the parameter name for CSRF token
   * 
   * @returns {string} The parameter name
   */
  getParameterName(): string {
    return this.parameterName
  }
}

// Export a singleton instance
export const csrfService = new CsrfService()
