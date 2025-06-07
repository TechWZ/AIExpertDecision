<script setup>
import { computed } from 'vue'
import { useStepsStore } from '@/stores/stepsStore'

const stepsStore = useStepsStore()
const active = computed(() => stepsStore.activeStep)
</script>

<template>
  <div class="steps-wrapper">
    <el-steps direction="vertical" :active="active" finish-status="success">
      <el-step title="决策需求" description="" />
      <el-step title="专家信息" description=""/>
      <el-step title="分析报告" description=""/>
    </el-steps>
  </div>
</template>

<style scoped>
.steps-wrapper {
  overflow: hidden;

  display: flex; 
  position: fixed;
  right: 0;
  top: 60px; /* header的高度 */
  bottom: 0;
}
</style>