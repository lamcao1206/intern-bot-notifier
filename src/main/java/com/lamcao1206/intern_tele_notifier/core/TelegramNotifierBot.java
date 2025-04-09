package com.lamcao1206.intern_tele_notifier.core;

import com.lamcao1206.intern_tele_notifier.config.TelegramBotConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class TelegramNotifierBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramBotConfiguration telegramBotConfiguration;
    private final TelegramClient telegramClient;
    
    @Autowired
    public TelegramNotifierBot(TelegramBotConfiguration telegramBotConfiguration) {
        this.telegramBotConfiguration = telegramBotConfiguration;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
    }


    @Override
    public String getBotToken() {
        return this.telegramBotConfiguration.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            if (messageText.equals("/start")) {
                sendMessage(chatId, "DOM Tracker Bot started! I'll notify you of changes to job entries.");
            } else if (messageText.equals("/stop")) {
                sendMessage(chatId, "Monitoring stopped.");
            }
        }
    }
    
    public void sendNotification(String text) {
        sendMessage(telegramBotConfiguration.getChatId(), text);
    }
    
    public void sendMessage(String chatId, String text) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            telegramClient.execute(message); 
        } catch (TelegramApiException e) {
            log.error("Error while sending message {}",e.getMessage(), e);
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }
}
