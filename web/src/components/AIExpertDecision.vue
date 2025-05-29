<script setup>
import ChatHistory from './ChatHistory.vue';
import Chat from './Chat.vue';
</script>
<template>
    <div class="app-container">
        <el-row class="main-row" :gutter="0">
            <el-col :xs="0" :sm="4" :md="4" :lg="5" :xl="6" class="sidebar-col">
                <ChatHistory />
            </el-col>
            <el-col :xs="24" :sm="20" :md="20" :lg="19" :xl="18" class="content-col">
                <Chat />
            </el-col>
        </el-row>
    </div>
</template>
<style scoped>
.app-container {
    width: 100vw;
    height: 100vh;
    overflow: hidden;
    margin: 0;
    padding: 0;
    background: #fafafa;
}

.main-row {
    height: 100vh;
    margin: 0 !important;
    overflow: hidden;
}

.sidebar-col {
    height: 100vh;
    border-right: 1px solid rgba(235, 238, 245, 0.8);
    overflow: hidden;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    transition: all 0.3s ease;
}

.content-col {
    height: 100vh;
    overflow: hidden;
    background: transparent;
}

/* 响应式设计 */
@media (max-width: 576px) {
    .sidebar-col {
        display: none;
    }
}

@media (min-width: 577px) and (max-width: 768px) {
    .sidebar-col {
        border-right: 1px solid rgba(235, 238, 245, 0.6);
    }
}

@media (min-width: 1200px) {
    .sidebar-col {
        border-right: 1px solid rgba(235, 238, 245, 0.8);
        box-shadow: 2px 0 8px rgba(0, 0, 0, 0.04);
    }
}
</style>