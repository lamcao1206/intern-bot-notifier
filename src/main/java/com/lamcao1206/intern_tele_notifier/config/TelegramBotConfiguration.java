package com.lamcao1206.intern_tele_notifier.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Configuration class for loading Telegram bot credentials
 * <p>This class read the `telegram_credentials.json` file located in the classpath
 * and extracts the neccessary credentials: bot username, authorization token and chatID.
 * </p>
 */

@Configuration
@Getter
public class TelegramBotConfiguration {
    @Value("classpath:telegram_credentials.json")
    private Resource credentialsResource;
    
    private String username;
    private String token;
    private String chatId;
    
    @PostConstruct
    private void loadCredentials() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(credentialsResource.getInputStream());
        this.username = jsonNode.get("telegrambot").get("username").asText();
        this.token = jsonNode.get("telegrambot").get("token").asText();
        this.chatId = jsonNode.get("chatId").asText();
    }
}
