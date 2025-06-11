import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useChatStore = defineStore('chat', () => {
  // State
  const sessions = ref([
    {
      id: 'session1',
      title: '与 AI 的第一次对话',
      lastMessage: '好的，下次再聊！',
      timestamp: '昨天 10:30',
      messages: [
        { id: 1, text: '你好！有什么可以帮助你的吗？', sender: 'bot', avatar: '/AigenMed.jpeg' },
        { id: 2, text: '我想了解一下 Vue3 的新特性。', sender: 'user', avatar: '/user-avatar.png' },
        { id: 3, text: 'Vue3 引入了 Composition API, Teleport, Fragments 等很棒的功能。', sender: 'bot', avatar: '/AigenMed.jpeg' },
        { id: 4, text: '好的，下次再聊！', sender: 'user', avatar: '/user-avatar.png' },
      ],
    },
    {
      id: 'session2',
      title: '关于 Element Plus 的提问', // This will be updated from backend
      lastMessage: '非常感谢您的帮助。', // This will be updated from backend
      timestamp: '2025年5月14日',    // This could also be updated
      messages: [ // These messages will be replaced by backend data
        { id: 1, text: 'Element Plus 的 el-table 如何实现自定义排序？', sender: 'user', avatar: '/user-avatar.png' },
        { id: 2, text: '你可以使用 `sort-method` 属性并提供一个自定义的排序函数。', sender: 'bot', avatar: '/AigenMed.jpeg' },
        { id: 3, text: '非常感谢您的帮助。', sender: 'user', avatar: '/user-avatar.png' },
      ],
    },
    {
      id: 'session3',
      title: '项目规划讨论',
      lastMessage: '这个组件很有用。',
      timestamp: '2025年5月12日',
      messages: [
        { id: 1, text: '我们项目下一步的计划是什么？', sender: 'user', avatar: '/user-avatar.png' },
        { id: 2, text: '计划是集成新的聊天历史功能。', sender: 'bot', avatar: '/AigenMed.jpeg' },
        { id: 3, text: '这个组件很有用。', sender: 'user', avatar: '/user-avatar.png' },
      ],
    },
  ]);

  // 初始化时不选择任何会话，以显示欢迎界面
  const activeSessionId = ref(null);

  // Getters (computed properties)
  const currentChatSession = computed(() => {
    return sessions.value.find(session => session.id === activeSessionId.value);
  });

  const currentChatMessages = computed(() => {
    const session = currentChatSession.value;
    return session ? session.messages : [];
  });

  // Actions
  // 创建新会话
  function createNewSession(title) {
    const now = new Date();
    const newSession = {
      id: 'session' + Date.now(),
      title: title || '新对话',
      lastMessage: '开始新的对话',
      timestamp: `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`,
      messages: []
    };
    
    sessions.value.unshift(newSession); // 添加到列表开头
    selectSession(newSession.id); // 自动选择新会话
    return newSession.id;
  }
  
  async function selectSession(sessionId) {
    activeSessionId.value = sessionId;

    if (!sessionId) {
      return; // 允许设置为 null 以显示欢迎界面
    }
    
    if (sessionId === 'session2') {
      const sessionToUpdate = sessions.value.find(s => s.id === 'session2');
      if (sessionToUpdate) {
        // Show a loading state immediately
        sessionToUpdate.messages = [{ id: Date.now(), text: '正在从服务器加载数据...', sender: 'system', avatar: '' }];
        try {
          // 通过Vite代理调用API
          const response = await fetch('/server/getChat');
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          const backendChatData = await response.json();

          // Update session details from backend
          sessionToUpdate.title = backendChatData.title || '来自服务器的会话';
          sessionToUpdate.lastMessage = backendChatData.lastMessagePreview || '已从服务器加载';
          // Example for timestamp, adjust formatting as needed
          // sessionToUpdate.timestamp = backendChatData.lastActivityTimestamp ? new Date(backendChatData.lastActivityTimestamp).toLocaleString('zh-CN') : new Date().toLocaleString('zh-CN');


          // Transform backend data into frontend message format
          sessionToUpdate.messages = [
            {
              id: Date.now(),
              text: `会话标题: ${backendChatData.title}`,
              sender: 'bot',
              avatar: '/AigenMed.jpeg'
            },
            {
              id: Date.now() + 1,
              text: `最新消息预览: ${backendChatData.lastMessagePreview}`,
              sender: 'bot',
              avatar: '/AigenMed.jpeg'
            },
            {
              id: Date.now() + 2,
              text: `(数据来自 sessionId: ${backendChatData.sessionId || 'N/A'})`,
              sender: 'system',
              avatar: ''
            }
          ];
        } catch (error) {
          console.error('Failed to fetch chat data from backend:', error);
          sessionToUpdate.messages = [
            { id: Date.now(), text: `无法加载会话数据: ${error.message}`, sender: 'system', avatar: '' }
          ];
        }
      }
    }
  }

  function addMessageToCurrentChat(messageText, senderType, customId = null) {
    const session = currentChatSession.value;
    if (session) {
      const newMessage = {
        id: customId || Date.now(),
        text: messageText,
        sender: senderType,
        avatar: senderType === 'user' ? '/user-avatar.png' : '/AigenMed.jpeg',
      };
      session.messages.push(newMessage);
      session.lastMessage = messageText.substring(0, 30) + (messageText.length > 30 ? '...' : '');
      session.timestamp = new Date().toLocaleString('zh-CN', { hour: '2-digit', minute: '2-digit', year: 'numeric', month: 'long', day: 'numeric' });

      // Prevent default bot auto-reply for session2 as its content is now backend-driven
      if (senderType === 'user' && session.id !== 'session2') {
        setTimeout(() => {
          const thinkingMessage = {
            id: Date.now() + 1,
            text: '正在思考...',
            sender: 'bot',
            avatar: '/AigenMed.jpeg',
          };
          session.messages.push(thinkingMessage);

          setTimeout(() => {
            const thinkingIndex = session.messages.findIndex(m => m.id === thinkingMessage.id);
            if (thinkingIndex !== -1) {
              session.messages.splice(thinkingIndex, 1);
            }

            const botResponse = {
              id: Date.now() + 2,
              text: `我已经收到你的消息: \"${messageText}\"。 但我现在还不能真正理解你。`,
              sender: 'bot',
              avatar: '/AigenMed.jpeg',
            };
            session.messages.push(botResponse);
            session.lastMessage = botResponse.text.substring(0, 30) + (botResponse.text.length > 30 ? '...' : '');
            session.timestamp = new Date().toLocaleString('zh-CN', { hour: '2-digit', minute: '2-digit', year: 'numeric', month: 'long', day: 'numeric' });
          }, 1500);
        }, 500);
      }
    }
  }

  // 添加根据ID删除消息的功能
  function removeMessage(messageId) {
    const session = currentChatSession.value;
    if (session) {
      const index = session.messages.findIndex(msg => msg.id === messageId);
      if (index !== -1) {
        session.messages.splice(index, 1);
      }
    }
  }

  return {
    sessions,
    activeSessionId,
    currentChatSession,
    currentChatMessages,
    selectSession,
    addMessageToCurrentChat,
    createNewSession,
    removeMessage,
  };
});
