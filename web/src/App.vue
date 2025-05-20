<script setup>
// import { createRouter } from 'vue-router' // This import seems unused here
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
</script>

<template>
  <el-container class="app-container">
    <Header />
    <el-main class="main-content">
      <router-view></router-view>
    </el-main>
    <Footer class="footer-content" />
  </el-container>
</template>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  /* padding: 0; Remove default padding if router-view or its children handle it */
  /* The common-layout div and its direct el-row/el-col might not be needed if Header is fixed or sticky */
}

.footer-content {
  flex-shrink: 0; /* Ensure footer doesn't shrink */
  /* Optional: Add some padding or margin if needed */
}

/* Remove old styles if they are no longer relevant */
/* header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
} */
</style>

<style>
/* Global styles if necessary, e.g., to ensure body/html take full height */
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>
