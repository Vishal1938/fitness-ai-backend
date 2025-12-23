package com.spring.ollama.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);
    private final ChatClient chatClient;

    public AiService(ChatClient chatClient) {
        this.chatClient = chatClient;
        logger.info("AiService initialized with ChatClient");
    }

    public String ask(String question) {
        logger.info("Received question: {}", question);
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Sending prompt to ChatClient");
            String response = chatClient
                    .prompt(question)
                    .call()
                    .content();

            long endTime = System.currentTimeMillis();
            logger.info("Successfully processed question in {} ms. Response length: {} characters",
                    (endTime - startTime), response.length());
            logger.debug("Response: {}", response);

            return response;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("Error processing question after {} ms: {}",
                    (endTime - startTime), question, e);
            throw new RuntimeException("Failed to get AI response", e);
        }
    }
}
