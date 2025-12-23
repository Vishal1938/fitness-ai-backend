package com.spring.ollama.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(ChatClientConfig.class);

    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel) {
        logger.info("Initializing ChatClient bean with OllamaChatModel");
        try {
            ChatClient client = ChatClient.builder(ollamaChatModel).build();
            logger.info("ChatClient bean created successfully");
            return client;
        } catch (Exception e) {
            logger.error("Failed to create ChatClient bean", e);
            throw e;
        }
    }
}