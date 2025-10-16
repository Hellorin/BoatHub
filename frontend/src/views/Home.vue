<template>
  <div class="home">
    <div class="hero">
      <h1>Welcome to BoatHub</h1>
      <p>Your comprehensive boat management platform</p>
      <router-link to="/boats" class="cta-button">
        View Boats
      </router-link>
    </div>
    
    <div class="features">
      <div class="feature-card">
        <h3>Boat Management</h3>
        <p>Manage your boat fleet with ease</p>
      </div>
      <div class="feature-card">
        <h3>Type Organization</h3>
        <p>Organize boats by type and category</p>
      </div>
      <div class="feature-card">
        <h3>Easy Updates</h3>
        <p>Update boat information quickly</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Home page component
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
}

.hero {
  text-align: center;
  padding: 4rem 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  margin-bottom: 3rem;
}

.hero h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.cta-button {
  display: inline-block;
  background-color: #3498db;
  color: white;
  padding: 1rem 2rem;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 600;
  transition: background-color 0.3s;
}

.cta-button:hover {
  background-color: #2980b9;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.feature-card h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.feature-card p {
  color: #7f8c8d;
  line-height: 1.6;
}
</style>

