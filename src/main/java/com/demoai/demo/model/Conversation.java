package com.demoai.demo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conversation {
    private String id;
    private String userId;
    private List<Message> messages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Conversation(String userId) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.messages = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addMessage(String content, String role) {
        Message message = new Message(content, role);
        this.messages.add(message);
        this.updatedAt = LocalDateTime.now();
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public static class Message {
        private String content;
        private String role;
        private LocalDateTime timestamp;

        public Message(String content, String role) {
            this.content = content;
            this.role = role;
            this.timestamp = LocalDateTime.now();
        }

        public String getContent() {
            return content;
        }

        public String getRole() {
            return role;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}