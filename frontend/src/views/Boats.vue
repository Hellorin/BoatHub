<template>
  <div class="boats">
    <div class="page-header">
      <h1>Boat Management</h1>
      <button class="add-boat-btn" @click="showAddForm = !showAddForm">
        Add New Boat
      </button>
    </div>

    <!-- Add Boat Form -->
    <div v-if="showAddForm" class="add-boat-form">
      <h3>Add New Boat</h3>
      <form @submit.prevent="addBoat">
        <div class="form-group">
          <label for="name">Boat Name:</label>
          <input 
            id="name"
            v-model="newBoat.name" 
            type="text" 
            required 
            placeholder="Enter boat name"
          />
        </div>
        <div class="form-group">
          <label for="description">Description:</label>
          <textarea 
            id="description"
            v-model="newBoat.description" 
            placeholder="Enter boat description"
          ></textarea>
        </div>
        <div class="form-group">
          <label for="type">Boat Type:</label>
          <select id="boatType" v-model="newBoat.boatType" required>
            <option value="">Select boat type</option>
            <option value="SAILBOAT">Sailboat</option>
            <option value="MOTORBOAT">Motorboat</option>
            <option value="YACHT">Yacht</option>
            <option value="SPEEDBOAT">Speedboat</option>
            <option value="FISHING_BOAT">Fishing boat</option>
            <option value="OTHER">Other</option>
          </select>
        </div>
        <div class="form-actions">
          <button type="submit" class="submit-btn">Add Boat</button>
          <button type="button" class="cancel-btn" @click="cancelAdd">Cancel</button>
        </div>
      </form>
    </div>

    <!-- Boats List -->
    <div class="boats-list">
      <div v-if="loading" class="loading">Loading boats...</div>
      <div v-else-if="boats.length === 0" class="no-boats">
        No boats found. Add your first boat!
      </div>
      <div v-else>
        <!-- Boats Table -->
        <div class="boats-table-container">
          <table class="boats-table">
            <thead>
              <tr>
                <th :class="['sortable', { active: sortBy === 'id' }]" @click="handleSort('id')">
                  ID
                  <span class="sort-icon">{{ getSortIcon('id') }}</span>
                </th>
                <th :class="['sortable', { active: sortBy === 'name' }]" @click="handleSort('name')">
                  Name
                  <span class="sort-icon">{{ getSortIcon('name') }}</span>
                </th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="boat in paginatedBoats" :key="boat.id" class="boat-row clickable-row" @click="openBoatModal(boat)">
                <td class="boat-id">{{ boat.id }}</td>
                <td class="boat-name">{{ boat.name }}</td>
                <td class="boat-actions" @click.stop>
                  <button @click="openBoatModal(boat)" class="view-btn">View</button>
                  <button @click="openBoatModal(boat, 'edit')" class="edit-btn">Edit</button>
                  <button @click="openBoatModal(boat, 'delete')" class="delete-btn">Delete</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination Controls -->
        <div class="pagination-container">
          <div class="pagination-info">
            Showing {{ startItem }} to {{ endItem }} of {{ totalElements }} boats
          </div>
          <div class="pagination-controls">
            <button 
              @click="goToPage(currentPage - 1)" 
              :disabled="currentPage === 1"
              class="pagination-btn"
            >
              Previous
            </button>
            <div class="page-numbers">
              <button
                v-for="page in visiblePages"
                :key="page"
                @click="goToPage(page)"
                :class="['page-btn', { active: page === currentPage }]"
              >
                {{ page }}
              </button>
            </div>
            <button 
              @click="goToPage(currentPage + 1)" 
              :disabled="currentPage === totalPages"
              class="pagination-btn"
            >
              Next
            </button>
          </div>
          <div class="items-per-page">
            <label for="itemsPerPage">Items per page:</label>
            <select id="itemsPerPage" v-model="itemsPerPage" @change="handleItemsPerPageChange">
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Boat Modal -->
    <div v-if="showBoatModal" class="modal-overlay" @click="closeBoatModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ modalMode === 'view' ? 'Boat Details' : modalMode === 'edit' ? 'Edit Boat' : 'Delete Boat' }}</h3>
          <button @click="closeBoatModal" class="close-btn">&times;</button>
        </div>
        
        <div class="modal-body">
          <!-- View Mode -->
          <div v-if="modalMode === 'view'" class="boat-details">
            <div class="detail-row">
              <label>ID:</label>
              <span>{{ selectedBoat?.id }}</span>
            </div>
            <div class="detail-row">
              <label>Name:</label>
              <span>{{ selectedBoat?.name }}</span>
            </div>
            <div class="detail-row">
              <label>Type:</label>
              <span class="type-badge">{{ selectedBoat?.boatType }}</span>
            </div>
            <div class="detail-row">
              <label>Description:</label>
              <span>{{ selectedBoat?.description || 'No description' }}</span>
            </div>
            <div class="detail-row">
              <label>Created:</label>
              <span>{{ formatDate(selectedBoat?.createdDate) }}</span>
            </div>
            <div class="detail-row">
              <label>Updated:</label>
              <span>{{ formatDate(selectedBoat?.updatedDate) }}</span>
            </div>
          </div>

          <!-- Edit Mode -->
          <div v-else-if="modalMode === 'edit'" class="edit-form">
            <div class="form-group">
              <label for="editName">Name:</label>
              <input 
                id="editName"
                v-model="editForm.name" 
                type="text" 
                required
                placeholder="Enter boat name"
              />
            </div>
            <div class="form-group">
              <label for="editType">Type:</label>
              <select id="editType" v-model="editForm.boatType" required>
                <option value="SAILBOAT">Sailboat</option>
                <option value="MOTORBOAT">Motorboat</option>
                <option value="YACHT">Yacht</option>
                <option value="SPEEDBOAT">Speedboat</option>
                <option value="FISHING_BOAT">Fishing Boat</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
            <div class="form-group">
              <label for="editDescription">Description:</label>
              <textarea 
                id="editDescription"
                v-model="editForm.description" 
                placeholder="Enter boat description"
                rows="3"
              ></textarea>
            </div>
          </div>

          <!-- Delete Mode -->
          <div v-else-if="modalMode === 'delete'" class="delete-confirmation">
            <p>Are you sure you want to delete this boat?</p>
            <div class="boat-info">
              <strong>{{ selectedBoat?.name }}</strong> ({{ selectedBoat?.boatType }})
            </div>
            <p class="warning-text">This action cannot be undone.</p>
          </div>
        </div>

        <div class="modal-footer">
          <button v-if="modalMode === 'view'" @click="closeBoatModal" class="close-btn-secondary">Close</button>
          <template v-else-if="modalMode === 'edit'">
            <button @click="saveEdit" class="save-btn" :disabled="!editForm.name.trim()">Save Changes</button>
            <button @click="closeBoatModal" class="cancel-btn">Cancel</button>
          </template>
          <template v-else-if="modalMode === 'delete'">
            <button @click="confirmDelete" class="delete-btn">Delete Boat</button>
            <button @click="closeBoatModal" class="cancel-btn">Cancel</button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useBoatStore } from '../stores/boatStore'
import type { Boat, CreateBoatRequest, UpdateBoatRequest, Pageable } from '../types'

// Use the boat store
const boatStore = useBoatStore()

// Local component state
const showAddForm = ref(false)

// Pagination state
const currentPage = ref(1)
const itemsPerPage = ref(10)

// Sorting state
const sortBy = ref('name')
const sortDirection = ref<'asc' | 'desc'>('asc')

// Modal state
const showBoatModal = ref(false)
const modalMode = ref<'view' | 'edit' | 'delete'>('view')
const selectedBoat = ref<Boat | null>(null)

// Editing state
const editForm = ref({
  name: '',
  description: '',
  boatType: ''
})

const newBoat = ref({
  name: '',
  description: '',
  boatType: ''
})

// Computed properties from store
const boats = computed(() => boatStore.boats)
const loading = computed(() => boatStore.loading)


// Pagination computed properties
const totalPages = computed(() => boatStore.currentPage?.totalPages || 0)
const totalElements = computed(() => boatStore.currentPage?.totalElements || 0)

const paginatedBoats = computed(() => boats.value)

const startItem = computed(() => {
  if (!boatStore.currentPage) return 0
  return boatStore.currentPage.pageable.pageNumber * boatStore.currentPage.pageable.pageSize + 1
})

const endItem = computed(() => {
  if (!boatStore.currentPage) return 0
  return startItem.value + boatStore.currentPage.numberOfElements - 1
})

const visiblePages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  
  // Show up to 5 page numbers
  let start = Math.max(1, current - 2)
  let end = Math.min(total, start + 4)
  
  // Adjust start if we're near the end
  if (end - start < 4) {
    start = Math.max(1, end - 4)
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
})

const loadBoats = async () => {
  const params: Pageable = {
    page: currentPage.value - 1, // Backend uses 0-based pagination
    size: itemsPerPage.value,
    sortBy: sortBy.value,
    sortDirection: sortDirection.value
  }
  await boatStore.fetchBoats(params)
}

// Pagination methods
const goToPage = async (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    await loadBoats()
  }
}

const handleItemsPerPageChange = async () => {
  currentPage.value = 1
  await loadBoats()
}

// Sorting methods
const handleSort = async (column: string) => {
  if (sortBy.value === column) {
    // If clicking the same column, toggle direction
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
  } else {
    // If clicking a different column, set it as sort column with ascending direction
    sortBy.value = column
    sortDirection.value = 'asc'
  }
  
  // Reset to first page when sorting
  currentPage.value = 1
  await loadBoats()
}

const getSortIcon = (column: string) => {
  if (sortBy.value !== column) {
    return '↕' // Neutral sort icon
  }
  return sortDirection.value === 'asc' ? '↑' : '↓'
}

// Modal methods
const openBoatModal = (boat: Boat, mode: 'view' | 'edit' | 'delete' = 'view') => {
  selectedBoat.value = boat
  modalMode.value = mode
  showBoatModal.value = true
  
  if (mode === 'edit') {
    editForm.value = {
      name: boat.name,
      description: boat.description || '',
      boatType: boat.boatType
    }
  }
}

const closeBoatModal = () => {
  showBoatModal.value = false
  selectedBoat.value = null
  modalMode.value = 'view'
  editForm.value = {
    name: '',
    description: '',
    boatType: ''
  }
}

const formatDate = (dateString?: string) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}


const addBoat = async () => {
  const boatData: CreateBoatRequest = {
    name: newBoat.value.name,
    description: newBoat.value.description,
    boatType: newBoat.value.boatType as any
  }
  
  try {
    await boatStore.addBoat(boatData)
    resetForm()
    // Go to last page to show the new boat
    currentPage.value = totalPages.value
    await loadBoats()
  } catch (error) {
    console.error('Error adding boat:', error)
  }
}

// Editing methods
const saveEdit = async () => {
  if (!selectedBoat.value) return
  
  // Validate that name is not empty
  if (!editForm.value.name.trim()) {
    alert('Boat name cannot be empty')
    return
  }
  
  const updateData: UpdateBoatRequest = {
    name: editForm.value.name.trim(),
    description: editForm.value.description.trim(),
    boatType: editForm.value.boatType as any
  }
  
  try {
    await boatStore.updateBoat(selectedBoat.value.id, updateData)
    closeBoatModal()
  } catch (error) {
    console.error('Error updating boat:', error)
  }
}

const confirmDelete = async () => {
  if (!selectedBoat.value) return
  
  try {
    await boatStore.deleteBoat(selectedBoat.value.id)
    closeBoatModal()
    // If current page becomes empty and it's not the first page, go to previous page
    if (paginatedBoats.value.length === 0 && currentPage.value > 1) {
      currentPage.value = currentPage.value - 1
      await loadBoats()
    }
  } catch (error) {
    console.error('Error deleting boat:', error)
  }
}


const cancelAdd = () => {
  resetForm()
}

const resetForm = () => {
  newBoat.value = { name: '', description: '', boatType: '' }
  showAddForm.value = false
}

onMounted(() => {
  loadBoats()
})
</script>

<style scoped>
.boats {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  color: #2c3e50;
}

.add-boat-btn {
  background-color: #27ae60;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.3s;
}

.add-boat-btn:hover {
  background-color: #229954;
}

.add-boat-form {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.add-boat-form h3 {
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #2c3e50;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group textarea {
  height: 100px;
  resize: vertical;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.submit-btn {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
}

.cancel-btn {
  background-color: #95a5a6;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
}

.boats-table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 2rem;
}

.boats-table {
  width: 100%;
  border-collapse: collapse;
}

.boats-table thead {
  background-color: #f8f9fa;
}

.boats-table th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #e9ecef;
}

.boats-table th.sortable {
  cursor: pointer;
  user-select: none;
  position: relative;
  transition: background-color 0.2s;
}

.boats-table th.sortable:hover {
  background-color: #f8f9fa;
}

.sort-icon {
  margin-left: 0.5rem;
  font-size: 0.9rem;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.boats-table th.sortable:hover .sort-icon {
  opacity: 1;
}

.boats-table th.sortable.active {
  background-color: #e3f2fd;
  color: #1976d2;
}

.boats-table th.sortable.active .sort-icon {
  opacity: 1;
  color: #1976d2;
}

.boats-table td {
  padding: 1rem;
  border-bottom: 1px solid #e9ecef;
  vertical-align: top;
}

.boat-row:hover {
  background-color: #f8f9fa;
}

.clickable-row {
  cursor: pointer;
  transition: all 0.2s ease;
}

.clickable-row:hover {
  background-color: #e3f2fd !important;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.clickable-row:active {
  transform: translateY(0);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.boat-id {
  font-weight: 600;
  color: #6c757d;
  width: 80px;
}

.boat-name {
  font-weight: 600;
  color: #2c3e50;
  min-width: 200px;
}

.boat-type {
  min-width: 120px;
}

.type-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  background-color: #e3f2fd;
  color: #1976d2;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 500;
  text-transform: capitalize;
}

.boat-description {
  color: #555;
  line-height: 1.5;
  max-width: 300px;
  word-wrap: break-word;
}

.boat-actions {
  display: flex;
  gap: 0.5rem;
  min-width: 200px;
  flex-wrap: wrap;
}

.view-actions,
.edit-actions {
  display: flex;
  gap: 0.5rem;
}

/* Edit mode styles */
.edit-input,
.edit-textarea,
.edit-select {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #007bff;
  border-radius: 4px;
  font-size: 0.9rem;
  background: white;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.edit-input:focus,
.edit-textarea:focus,
.edit-select:focus {
  outline: none;
  border-color: #0056b3;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
}

.edit-textarea {
  resize: vertical;
  min-height: 60px;
  font-family: inherit;
}

.edit-select {
  cursor: pointer;
}

/* Edit mode row styling */
.boat-row.editing {
  background-color: #f8f9ff;
}

.boat-row.editing td {
  border-color: #007bff;
}

/* Button styles for edit mode */
.save-btn {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: background-color 0.2s;
}

.save-btn:hover {
  background-color: #218838;
}

.cancel-edit-btn {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: background-color 0.2s;
}

.cancel-edit-btn:hover {
  background-color: #5a6268;
}

.edit-btn {
  background-color: #f39c12;
  color: white;
  border: none;
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: background-color 0.2s;
}

.edit-btn:hover {
  background-color: #e67e22;
}

.delete-btn {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: background-color 0.2s;
}

.delete-btn:hover {
  background-color: #c0392b;
}

.loading, .no-boats {
  text-align: center;
  padding: 3rem;
  color: #7f8c8d;
  font-size: 1.1rem;
}

/* Pagination Styles */
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  flex-wrap: wrap;
  gap: 1rem;
}

.pagination-info {
  color: #6c757d;
  font-size: 0.9rem;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.pagination-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #dee2e6;
  background: white;
  color: #495057;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #e9ecef;
  border-color: #adb5bd;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 0.25rem;
  margin: 0 0.5rem;
}

.page-btn {
  padding: 0.5rem 0.75rem;
  border: 1px solid #dee2e6;
  background: white;
  color: #495057;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  min-width: 40px;
  transition: all 0.2s;
}

.page-btn:hover {
  background-color: #e9ecef;
  border-color: #adb5bd;
}

.page-btn.active {
  background-color: #007bff;
  border-color: #007bff;
  color: white;
}

.items-per-page {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #6c757d;
}

.items-per-page select {
  padding: 0.25rem 0.5rem;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  background: white;
  color: #495057;
  font-size: 0.9rem;
}

/* Responsive Design */
@media (max-width: 768px) {
  .pagination-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .pagination-controls {
    justify-content: center;
  }
  
  .boats-table {
    font-size: 0.875rem;
  }
  
  .boats-table th,
  .boats-table td {
    padding: 0.75rem 0.5rem;
  }
  
  .boat-description {
    max-width: 200px;
  }
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #6c757d;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: #f8f9fa;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding: 1.5rem;
  border-top: 1px solid #e9ecef;
  background-color: #f8f9fa;
}

/* Boat Details Styles */
.boat-details {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.detail-row label {
  font-weight: 600;
  color: #2c3e50;
  min-width: 100px;
}

.detail-row span {
  color: #555;
}

/* Edit Form Styles */
.edit-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.edit-form .form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.edit-form label {
  font-weight: 600;
  color: #2c3e50;
}

.edit-form input,
.edit-form select,
.edit-form textarea {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.edit-form input:focus,
.edit-form select:focus,
.edit-form textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.edit-form textarea {
  resize: vertical;
  min-height: 80px;
}

/* Delete Confirmation Styles */
.delete-confirmation {
  text-align: center;
  padding: 1rem 0;
}

.delete-confirmation p {
  margin-bottom: 1rem;
  color: #555;
}

.boat-info {
  background-color: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
  border-left: 4px solid #dc3545;
}

.warning-text {
  color: #dc3545;
  font-weight: 600;
  margin: 0;
}

/* Button Styles */
.view-btn {
  background-color: #17a2b8;
  color: white;
  border: none;
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: background-color 0.2s;
}

.view-btn:hover {
  background-color: #138496;
}

.close-btn-secondary {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.close-btn-secondary:hover {
  background-color: #5a6268;
}
</style>

