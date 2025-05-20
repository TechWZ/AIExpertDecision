package institute.pojo;

import java.time.LocalDateTime;

/**
 * 表示一个聊天会话。
 * 一个会话将多个聊天消息分组，并包含标题和最后活动的跟踪信息。
 * 此实体是根据前端 Vue 应用程序 (chatStore.js) 中观察到的结构设计的。
 */
public class Chat {

    private Long id; // 数据库主键
    private String sessionId; // 会话的唯一标识符，可能来自客户端或生成（例如 UUID 或 "session1"）
    private String title; // 聊天会话的标题 (例如, "与 AI 的第一次对话")
    private String lastMessagePreview; // 会话中最后交换消息的预览
    private LocalDateTime lastActivityTimestamp; // 最后一条消息或交互的时间戳
    // private Long userId; // 可选：拥有此会话的用户的标识符

    // 可选：如果消息要直接成为此实体的一部分。
    // 如果使用 JPA，这通常会涉及 @OneToMany 关系，
    // 并且您需要一个 ChatMessage 实体（此文件之前的内容定义了该实体）。
    // private List<ChatMessage> messages;

    // 构造函数
    public Chat() {
        this.lastActivityTimestamp = LocalDateTime.now(); // 创建时默认为当前时间
    }

    public Chat(String sessionId, String title, String lastMessagePreview) {
        this.sessionId = sessionId;
        this.title = title;
        this.lastMessagePreview = lastMessagePreview;
        this.lastActivityTimestamp = LocalDateTime.now();
    }

    // Getters 和 Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastMessagePreview() {
        return lastMessagePreview;
    }

    public void setLastMessagePreview(String lastMessagePreview) {
        this.lastMessagePreview = lastMessagePreview;
    }

    public LocalDateTime getLastActivityTimestamp() {
        return lastActivityTimestamp;
    }

    public void setLastActivityTimestamp(LocalDateTime lastActivityTimestamp) {
        this.lastActivityTimestamp = lastActivityTimestamp;
    }

    // public Long getUserId() {
    //     return userId;
    // }
    //
    // public void setUserId(Long userId) {
    //     this.userId = userId;
    // }

    // public List<ChatMessage> getMessages() {
    //     return messages;
    // }
    //
    // public void setMessages(List<ChatMessage> messages) {
    //     this.messages = messages;
    // }

    @Override
    public String toString() {
        return "Chat{" + // 从 ChatSession 更改为 Chat 以匹配类名
               "id=" + id +
               ", sessionId='" + sessionId + '\'' +
               ", title='" + title + '\'' +
               ", lastMessagePreview='" + lastMessagePreview + '\'' +
               ", lastActivityTimestamp=" + lastActivityTimestamp +
            //    (userId != null ? ", userId=" + userId : "") +
               '}';
    }

    // 考虑基于 sessionId 或 id 实现 equals() 和 hashCode() 以进行正确的实体管理。
}
