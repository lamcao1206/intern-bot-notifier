package com.lamcao1206.intern_tele_notifier.core;

import com.lamcao1206.intern_tele_notifier.config.TelegramBotConfiguration;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class TelegramNotifierBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramBotConfiguration telegramBotConfiguration;
    private final TelegramClient telegramClient;
    private final JobTrackingService jobTrackingService;
    private final TaskScheduler taskScheduler;

    private volatile boolean isMonitoring = false;
    private ScheduledFuture<?> scheduledTask;

    @Value("${time.duration:10}") // Default to 10 seconds if not specified
    private int duration;

    @Autowired
    public TelegramNotifierBot(TelegramBotConfiguration telegramBotConfiguration,
                               JobTrackingService jobTrackingService,
                               TaskScheduler taskScheduler) {
        this.telegramBotConfiguration = telegramBotConfiguration;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
        this.jobTrackingService = jobTrackingService;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public String getBotToken() {
        return this.telegramBotConfiguration.getToken();
    }

    @Override
    public LongPollingSingleThreadUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            log.info("Received command: {} from chatId: {}", messageText, chatId);
            if (messageText.equals("/start")) {
                startMonitoring();
            } else if (messageText.equals("/stop")) {
                stopMonitoring();
            }
        }
    }

    private void startMonitoring() {
        if (!isMonitoring) {
            log.info("Scheduling task with duration: {} seconds", duration);
            scheduledTask = taskScheduler.scheduleAtFixedRate(() -> {
                log.info("Task executed at: {}", System.currentTimeMillis());
                String message = jobTrackingService.checkDomChanges();
                log.info("checkDomChanges result: {}", message != null ? message : "No change");
                if (message != null) {
                    sendNotification(message);
                }
            }, Duration.ofSeconds(duration));
            isMonitoring = true;
            sendNotification("Job tracking started...");
            log.info("Job tracking started on chatId {}", telegramBotConfiguration.getChatId());
        } else {
            sendNotification("Job tracking is already running");
            log.info("Attempted to start job tracking, but it’s already running");
        }
    }

    private void stopMonitoring() {
        if (isMonitoring && scheduledTask != null) {
            scheduledTask.cancel(false);
            isMonitoring = false;
            sendNotification("Job tracking stopped...");
            log.info("Job tracking stopped on chatId {}", telegramBotConfiguration.getChatId());
        } else {
            sendNotification("Job tracking is not running");
            log.info("Attempted to stop job tracking, but it’s not running");
        }
    }

    public void sendNotification(String text) {
        sendMessage(telegramBotConfiguration.getChatId(), text);
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error while sending message: {}", e.getMessage(), e);
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("Registered bot running state is: {}", botSession.isRunning());
    }

    @PreDestroy
    public void cleanup() {
        if (isMonitoring && scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(false);
            log.info("Scheduled task cancelled during shutdown");
        }
    }
}