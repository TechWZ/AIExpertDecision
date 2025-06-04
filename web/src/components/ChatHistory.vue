<script setup>
import { computed } from 'vue';
import { useChatStore } from '@/stores/chatStore'; // Adjusted path

const chatStore = useChatStore();

const chatSessions = computed(() => chatStore.sessions);
const activeSessionId = computed(() => chatStore.activeSessionId);

const selectSession = (session) => {
  chatStore.selectSession(session.id);
};

const addNewChat = () => {
  // Placeholder for new chat functionality
  // You might want to add a new session to the store
  console.log('New chat button clicked');
  // Example: chatStore.createNewSession(); (You'd need to implement this in the store)
};
</script>

<template>
      <el-card shadow="never" class="history-card">
        <template #header>
          <div class="card-header">
            <span>会话记录</span>
            <el-button type="primary" plain size="small" @click="addNewChat">新对话</el-button>
          </div>
        </template>
        <el-menu :default-active="activeSessionId" class="history-menu">
          <el-menu-item 
            v-for="session in chatSessions" 
            :key="session.id" 
            :index="session.id" 
            @click="selectSession(session)"
            :class="{ 'is-active': session.id === activeSessionId }"
          >
            <template #title>
              <div class="session-item">
                <span class="session-title">{{ session.title }}</span>
                <span class="session-last-message">{{ session.lastMessage }}</span>
                <span class="session-timestamp">{{ session.timestamp }}</span>
              </div>
            </template>
          </el-menu-item>
        </el-menu>
      </el-card>
</template>

<style scoped>
.history-card {
  border-radius: 0;
  border: none;
}

.history-card :deep(.el-card__header) {
  padding: 10px 15px;
  border-bottom: 1px solid #ebeef5;
}

.history-card :deep(.el-card__body) {
  padding: 0;
  height: calc(100vh - 60px);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.history-menu {
  border-right: none;
}

.history-menu .el-menu-item {
  height: auto;
  line-height: normal;
  padding: 10px 15px !important;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  border-bottom: 1px solid #f0f0f0;
}

.history-menu .el-menu-item.is-active {
  background-color: #ecf5ff;
  color: #409EFF;
}

.history-menu .el-menu-item:last-child {
  border-bottom: none;
}

.session-item {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.session-title {
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-last-message {
  font-size: 12px;
  color: #606266;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-timestamp {
  font-size: 10px;
  color: #909399;
}
</style>
