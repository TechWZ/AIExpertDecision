<template>
    <el-row style="background-color: #3a5ba0; height: 100%; ">
      <el-col :lg="2" :md="3" class="logo-container">
        <div class="logo-background">
          <img src="/AigenMed.svg" alt="AigenMed Logo" class="logo-img" />
        </div>
      </el-col>
      <el-col :lg="22" :md="21" class="title-container">
        <div class="titles">
          <span class="main-title">国家健康医疗大数据研究院</span>
          <el-divider direction="vertical" class="title-divider"></el-divider>
          <span class="sub-title">医学研究设计与分析AI智能体平台</span>
          <el-divider direction="vertical" class="title-divider"></el-divider>
          <span class="en-title">AI Agent Platform for Medical Research Design and Analysis</span>
        </div>
      </el-col>
    </el-row>
</template>

<script setup>

</script>

<style scoped>
.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.logo-background {
  width: 60px;
  height: 60px;
  background-color: #f5f5f5;
  border-radius: 50%;
  padding: 4px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-img {
  height: 40px;
  object-fit: contain;
  max-width: 100%;
  max-height: 100%;
}

.title-container {
  display: flex;
  align-items: center;
  height: 100%;
  padding-left: 15px; /* Adjusted padding */
}

.titles {
  display: flex;
  align-items: center;
  justify-content: space-between; /* Changed from flex-start to space-between */
  color: white;
  white-space: nowrap;
  width: 100%;
}

.main-title {
  font-size: 1.5rem; /* Desktop: H5 / MUI variant="h5" */
  font-weight: bold;
  letter-spacing: 1px;
  flex-shrink: 2; /* More likely to shrink than sub-title */
}

.sub-title {
  font-size: 1.8rem; /* Desktop: Larger, like MUI variant="h4" */
  font-weight: bold;
  flex-shrink: 1; /* Resists shrinking the most */
}

.en-title {
  font-size: 1.2rem; /* Desktop: Smaller, like MUI variant="h6" */
  color: rgba(255, 255, 255, 0.9);
  flex-shrink: 3; /* Most likely to shrink and show ellipsis */
  text-overflow: ellipsis;
  white-space: nowrap; /* Ensure it stays on one line for ellipsis */
}

.title-divider {
  background-color: rgba(255, 255, 255, 0.5);
  height: 28px; /* Adjusted height */
  align-self: center;
  margin: 0 15px; /* Increased margin for more spacing */
}
</style>
