<template>
  <el-header class="header-container" height="80px">
    <el-row align="middle" style="height: 100%;">
      <el-col :lg="2" :md="3" :sm="3" :xs="4" class="logo-container">
        <div class="logo-background">
          <img src="/AigenMed.svg" alt="AigenMed Logo" class="logo-img" />
        </div>
      </el-col>
      <el-col :lg="22" :md="21" :sm="21" :xs="20" class="title-container">
        <div class="titles">
          <span class="main-title">国家健康医疗大数据研究院</span>
          <el-divider direction="vertical" class="title-divider"></el-divider>
          <span class="sub-title">医学研究设计与分析AI智能体平台</span>
          <el-divider direction="vertical" class="title-divider" v-if="!isMobile"></el-divider>
          <span class="en-title" v-if="!isMobile">AI Agent Platform for Medical Research Design and Analysis</span>
        </div>
      </el-col>
    </el-row>
  </el-header>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

const isMobile = ref(false);

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768; // Adjust breakpoint as needed (md in MUI is often 960px, sm is 600px)
};

onMounted(() => {
  checkMobile();
  window.addEventListener('resize', checkMobile);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', checkMobile);
});
</script>

<style scoped>
.header-container {
  background-color: #3a5ba0;
  padding: 0 20px;
  box-shadow: none;
  width: 100%;
}

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
  overflow: hidden;
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
  overflow: hidden;
}

.titles {
  display: flex;
  align-items: center;
  justify-content: space-between; /* Changed from flex-start to space-between */
  color: white;
  white-space: nowrap;
  overflow: hidden;
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
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap; /* Ensure it stays on one line for ellipsis */
}

.title-divider {
  background-color: rgba(255, 255, 255, 0.5);
  height: 28px; /* Adjusted height */
  align-self: center;
  margin: 0 15px; /* Increased margin for more spacing */
}

/* Medium screens / Tablets */
@media (max-width: 1024px) {
  .title-container {
    padding-left: 10px;
  }
  .main-title {
    font-size: 1.3rem;
    letter-spacing: 0.5px;
  }
  .sub-title {
    font-size: 1.5rem;
  }
  .en-title {
    font-size: 1.0rem;
  }
  .title-divider {
    margin: 0 10px;
    height: 24px;
  }
}

/* Small screens / Mobile (corresponds to isMobile=true where en-title is hidden) */
@media (max-width: 767px) {
  .header-container {
    padding: 0 10px;
  }
  .logo-background {
    width: 50px;
    height: 50px;
  }
  .logo-img {
    height: 30px;
  }
  .title-container {
    padding-left: 8px;
  }
  .titles {
    /* justify-content: center; /* Optional: center if titles are short */
  }
  .main-title {
    font-size: 0.9rem; /* MUI Subtitle1 approx */
    letter-spacing: 0.25px;
  }
  .sub-title {
    font-size: 1.0rem; /* MUI Body1 approx, but slightly larger for emphasis */
  }
  /* en-title is hidden by v-if="!isMobile" */
  .title-divider {
    margin: 0 8px;
    height: 20px;
  }
}
</style>
