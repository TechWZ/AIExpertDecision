<script setup>
// import { createRouter } from 'vue-router' // This import seems unused here
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import ChatHistory from './components/ChatHistory.vue';
import Steps from './components/Steps.vue'
</script>

<template>
  <div class="common-layout">
    <el-header style="padding: 0;">
      <Header />
    </el-header>
    <el-container>
      <el-aside width="10%">
        <!-- <ChatHistory /> -->
      </el-aside>

      <el-main style="padding: 0; overflow: hidden;">
        <router-view></router-view>
      </el-main>

      <el-aside width="10%">
        <Steps />
      </el-aside>
    </el-container>
  </div>
</template>

<style scoped>
</style>