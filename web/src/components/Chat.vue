<script setup>
import { ref, nextTick, watch, onMounted, computed } from 'vue';
import { Promotion } from '@element-plus/icons-vue';
import { useChatStore } from '@/stores/chatStore'; 

const chatStore = useChatStore();
const newMessage = ref('');
const messageContainer = ref(null); 

// Get messages from the store
const messages = computed(() => chatStore.currentChatMessages);
const activeSessionId = computed(() => chatStore.activeSessionId); // Make sure activeSessionId is available

const scrollToBottom = async () => {
  await nextTick();
  if (messageContainer.value && messageContainer.value.wrapRef) {
    const scrollWrapper = messageContainer.value.wrapRef;
    // A small delay can sometimes help ensure the DOM has fully updated
    setTimeout(() => {
        scrollWrapper.scrollTop = scrollWrapper.scrollHeight;
    }, 0);
  } else if (messageContainer.value && typeof messageContainer.value.setScrollTop === 'function') {
    // Fallback for older or different el-scrollbar versions if wrapRef is not directly available
    // This might require checking the specific version of Element Plus if issues persist
    const internalScrollbar = messageContainer.value.scrollbar$; // Example, might vary
    if (internalScrollbar && internalScrollbar.wrapRef) {
        internalScrollbar.wrapRef.scrollTop = internalScrollbar.wrapRef.scrollHeight;
    }
  }
};

const sendMessage = () => {
  if (newMessage.value.trim() === '') return;
  chatStore.addMessageToCurrentChat(newMessage.value, 'user');
  newMessage.value = '';
  // scrollToBottom will be triggered by the watch on messages
};

// Scroll to bottom when messages change or the active session changes
watch(
  () => messages.value,
  () => {
    scrollToBottom();
  },
  { deep: true, immediate: true } // immediate true to scroll on initial load of a session
);

// Initial scroll to bottom if messages are already present (e.g., when switching sessions)
onMounted(() => {
  // Ensure store is initialized and potentially a session is active
  if (messages.value.length > 0) {
    scrollToBottom();
  }
});

</script>
<template>
  <el-container class="chat-container">
    <el-main class="chat-messages-area">
      <el-scrollbar ref="messageContainer" height="100%">
        <div v-if="!activeSessionId || messages.length === 0" class="no-messages">
          <p v-if="!activeSessionId">请从左侧选择一个会话开始聊天。</p>
          <p v-else>当前会话没有消息。发送一条消息开始吧！</p>
        </div>
        <div v-else v-for="message in messages" :key="message.id" class="message-item" :class="['message-' + message.sender]">
          <el-avatar :src="message.sender === 'bot' ? '/AigenMed.jpeg' : message.avatar" class="message-avatar"></el-avatar>
          <div class="message-content">
            <el-card shadow="hover" class="message-card">
              {{ message.text }}
            </el-card>
          </div>
        </div>
      </el-scrollbar>
    </el-main>
    <el-footer class="chat-input-area">
      <el-input
        v-model="newMessage"
        placeholder="输入消息..."
        @keyup.enter="sendMessage"
        clearable
        size="large"
        :disabled="!chatStore.activeSessionId"
      >
        <template #append>
          <el-button :icon="Promotion" @click="sendMessage" type="primary" :disabled="!chatStore.activeSessionId" />
        </template>
      </el-input>
    </el-footer>
  </el-container>
</template>
<style scoped>
.chat-container {
  height: calc(100vh - 100px); /* Adjust based on your layout, assuming some header/footer elsewhere */
  display: flex;
  flex-direction: column;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden; /* Ensures footer stays at bottom */
}

.chat-messages-area {
  flex-grow: 1;
  padding: 20px;
  overflow-y: auto; /* This will be handled by el-scrollbar */
  background-color: #f9fafb;
  position: relative; /* Needed for absolute positioning of debug line */
}

.message-item {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-end; /* Align avatar with bottom of text card */
}

.message-avatar {
  margin-right: 10px;
  margin-left: 10px;
  flex-shrink: 0; /* Prevent avatar from shrinking */
}

.message-user {
  flex-direction: row-reverse; /* User messages on the right */
}

.message-user .message-avatar {
  margin-left: 10px;
  margin-right: 0;
}

.message-user .message-content {
  align-items: flex-end; /* Align card to the right */
}

.message-user .message-card {
  background-color: #d9ecff; /* Light blue for user messages */
  border-color: #d9ecff;
}

.message-content {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.message-card {
  padding: 10px 15px;
  border-radius: 10px;
  word-wrap: break-word; /* Ensure long words break */
  background-color: #ffffff; /* White for bot messages */
  border: 1px solid #e4e7ed;
}


.chat-input-area {
  padding: 15px 20px;
  background-color: #ffffff;
  border-top: 1px solid #ebeef5;
  height: auto; /* Allow footer to size based on content */
  display: flex; /* Use flex for better alignment */
  align-items: center; /* Center items vertically */
}

.chat-input-area .el-input {
  flex-grow: 1; /* Input takes available space */
}

.chat-input-area .el-button {
  margin-left: 10px; /* Space between input and button */
}

/* Ensure el-scrollbar takes full height of its container */
.el-scrollbar {
  height: 100%;
}

.el-scrollbar :deep(.el-scrollbar__wrap) {
  overflow-x: hidden; /* Hide horizontal scrollbar */
}

.no-messages {
  display: flex;
  flex-direction: column; /* Stack paragraphs */
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
  font-size: 16px;
}
</style>
