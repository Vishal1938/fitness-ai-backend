package com.spring.ollama.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Optional service to maintain conversation history
 * Use this if you want the AI to remember previous messages in a conversation
 */
@Service
public class ConversationMemoryService {

    // Store conversation history per session/user
    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    // Maximum messages to keep in memory per conversation
    private static final int MAX_HISTORY_SIZE = 10;

    /**
     * Add a message to conversation history
     */
    public void addMessage(String conversationId, String role, String content) {
        conversationHistory.computeIfAbsent(conversationId, k -> new ArrayList<>())
                .add(new Message(role, content));

        // Limit history size
        List<Message> history = conversationHistory.get(conversationId);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0); // Remove oldest message
        }
    }

    /**
     * Get conversation history as formatted string
     */
    public String getConversationContext(String conversationId) {
        List<Message> history = conversationHistory.get(conversationId);
        if (history == null || history.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder("\n\nPrevious conversation:\n");
        for (Message msg : history) {
            context.append(msg.role).append(": ").append(msg.content).append("\n");
        }
        return context.toString();
    }

    /**
     * Clear conversation history
     */
    public void clearConversation(String conversationId) {
        conversationHistory.remove(conversationId);
    }

    /**
     * Get all conversation IDs
     */
    public Set<String> getAllConversationIds() {
        return conversationHistory.keySet();
    }

    /**
     * Inner class to represent a message
     */
    private static class Message {
        String role;
        String content;

        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}