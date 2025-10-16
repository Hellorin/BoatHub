import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import { csrfService } from './services/csrfService'

const app = createApp(App)

app.use(createPinia())
app.use(router)

// Initialize CSRF service
csrfService.initialize().catch(error => {
  console.error('Failed to initialize CSRF service:', error)
})

app.mount('#app')

