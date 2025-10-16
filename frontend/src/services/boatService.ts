import { apiRequest, buildQueryString } from './api'
import type { 
  Boat, 
  CreateBoatRequest, 
  UpdateBoatRequest, 
  Pageable, 
  Page 
} from '../types'

/**
 * Service class for boat-related API operations
 */
export class BoatService {
  private readonly baseEndpoint = '/api/v1/boats'

  /**
   * Fetches a paginated list of boats from the backend
   */
  async getBoats(params: Pageable): Promise<Page<Boat>> {
    try {
      const queryParams = {
        page: params.page,
        size: params.size,
        ...(params.sortBy && { sortBy: params.sortBy }),
        ...(params.sortDirection && { sortDirection: params.sortDirection })
      }
      
      const queryString = buildQueryString(queryParams)
      const endpoint = `${this.baseEndpoint}${queryString}`
      
      return await apiRequest<Page<Boat>>(endpoint, {
        method: 'GET'
      })
    } catch (error) {
      console.error('Error fetching boats:', error)
      throw error
    }
  }

  /**
   * Fetches a single boat by ID
   */
  async getBoatById(id: number): Promise<Boat> {
    try {
      return await apiRequest<Boat>(`${this.baseEndpoint}/${id}`, {
        method: 'GET'
      })
    } catch (error) {
      console.error(`Error fetching boat ${id}:`, error)
      throw error
    }
  }

  /**
   * Creates a new boat
   */
  async createBoat(boatData: CreateBoatRequest): Promise<Boat> {
    try {
      return await apiRequest<Boat>(this.baseEndpoint, {
        method: 'POST',
        body: JSON.stringify(boatData)
      })
    } catch (error) {
      console.error('Error creating boat:', error)
      throw error
    }
  }

  /**
   * Updates an existing boat with complete boat data.
   * Requires all fields (name, description, boatType) to be provided.
   */
  async updateBoat(id: number, boatData: UpdateBoatRequest): Promise<Boat> {
    try {
      return await apiRequest<Boat>(`${this.baseEndpoint}/${id}`, {
        method: 'PUT',
        body: JSON.stringify(boatData)
      })
    } catch (error) {
      console.error(`Error updating boat ${id}:`, error)
      throw error
    }
  }

  /**
   * Deletes a boat by ID
   */
  async deleteBoat(id: number): Promise<void> {
    try {
      await apiRequest<void>(`${this.baseEndpoint}/${id}`, {
        method: 'DELETE'
      })
    } catch (error) {
      console.error(`Error deleting boat ${id}:`, error)
      throw error
    }
  }

}

// Export a singleton instance
export const boatService = new BoatService()
