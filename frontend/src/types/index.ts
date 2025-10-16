export interface Boat {
  id: number
  name: string
  description?: string
  boatType: BoatType
  createdDate: string
  updatedDate: string
}

export type BoatType = 'SAILBOAT' | 'MOTORBOAT' | 'YACHT' | 'SPEEDBOAT' | 'FISHING_BOAT' | 'OTHER'

export interface CreateBoatRequest {
  name: string
  description?: string
  boatType: BoatType
}

export interface UpdateBoatRequest {
  name: string
  description?: string
  boatType: BoatType
}

// Pagination types
export interface Pageable {
  page: number
  size: number
  sortBy?: string
  sortDirection?: 'asc' | 'desc'
}

export interface Page<T> {
  content: T[]
  pageable: {
    pageNumber: number
    pageSize: number
    sort: {
      sorted: boolean
      unsorted: boolean
      empty: boolean
    }
  }
  totalElements: number
  totalPages: number
  last: boolean
  first: boolean
  numberOfElements: number
  size: number
  number: number
  sort: {
    sorted: boolean
    unsorted: boolean
    empty: boolean
  }
  empty: boolean
}

export interface ApiResponse<T> {
  data: T
  message?: string
}

export interface ApiError {
  message: string
  status: number
  timestamp?: string
  path?: string
}

