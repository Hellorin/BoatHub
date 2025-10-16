import { defineStore } from 'pinia'
import { ref } from 'vue'
import { boatService } from '../services/boatService'
import type { Boat, CreateBoatRequest, UpdateBoatRequest, Pageable, Page } from '../types'

export const useBoatStore = defineStore('boat', () => {
  const boats = ref<Boat[]>([])
  const currentPage = ref<Page<Boat> | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  /**
   * Fetches boats with pagination from the backend
   */
  const fetchBoats = async (params: Pageable) => {
    loading.value = true
    error.value = null
    
    try {
      const page = await boatService.getBoats(params)
      currentPage.value = page
      boats.value = page.content
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch boats'
      console.error('Error fetching boats:', err)
    } finally {
      loading.value = false
    }
  }

  /**
   * Fetches a single boat by ID
   */
  const fetchBoatById = async (id: number) => {
    loading.value = true
    error.value = null
    
    try {
      return await boatService.getBoatById(id)
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to fetch boat'
      console.error('Error fetching boat:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Creates a new boat
   */
  const addBoat = async (boatData: CreateBoatRequest) => {
    loading.value = true
    error.value = null
    
    try {
      const newBoat = await boatService.createBoat(boatData)
      // Add to current boats list if we have a current page
      if (currentPage.value) {
        boats.value.push(newBoat)
        currentPage.value.content.push(newBoat)
        currentPage.value.totalElements += 1
      }
      return newBoat
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to add boat'
      console.error('Error adding boat:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Updates an existing boat
   */
  const updateBoat = async (id: number, updates: UpdateBoatRequest) => {
    loading.value = true
    error.value = null
    
    try {
      const updatedBoat = await boatService.updateBoat(id, updates)
      // Update in current boats list
      const index = boats.value.findIndex(boat => boat.id === id)
      if (index !== -1) {
        boats.value[index] = updatedBoat
        if (currentPage.value) {
          const pageIndex = currentPage.value.content.findIndex(boat => boat.id === id)
          if (pageIndex !== -1) {
            currentPage.value.content[pageIndex] = updatedBoat
          }
        }
      }
      return updatedBoat
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to update boat'
      console.error('Error updating boat:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Deletes a boat
   */
  const deleteBoat = async (id: number) => {
    loading.value = true
    error.value = null
    
    try {
      await boatService.deleteBoat(id)
      // Remove from current boats list
      boats.value = boats.value.filter(boat => boat.id !== id)
      if (currentPage.value) {
        currentPage.value.content = currentPage.value.content.filter(boat => boat.id !== id)
        currentPage.value.totalElements -= 1
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Failed to delete boat'
      console.error('Error deleting boat:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    boats,
    currentPage,
    loading,
    error,
    fetchBoats,
    fetchBoatById,
    addBoat,
    updateBoat,
    deleteBoat
  }
})

