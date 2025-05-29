<script setup>
// import { createRouter } from 'vue-router' // This import seems unused here
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
</script>

<template>
  <div id="app-wrapper">
    <el-container class="app-container">
      <Header />
      <el-main class="main-content">
        <router-view></router-view>
      </el-main>
      <Footer class="footer-content" />
    </el-container>
  </div>
</template>

<style scoped>
#app-wrapper {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.main-content {
  flex: 1;
  padding: 0;
  overflow: hidden;
}

.footer-content {
  flex-shrink: 0;
}
</style>

<style>
/* Global styles if necessary, e.g., to ensure body/html take full height */
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
}
</style>
