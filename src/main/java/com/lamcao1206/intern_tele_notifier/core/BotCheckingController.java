package com.lamcao1206.intern_tele_notifier.core;

import com.lamcao1206.intern_tele_notifier.config.TelegramBotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/check")
public class BotCheckingController {
    @Autowired
    private TelegramNotifierBot bot;
    
    @Autowired
    private TelegramBotConfiguration botConfiguration;
    
    
    @GetMapping("/status")
    public String checkBotStatus() {
        return "Bot is running with username: " + botConfiguration.getUsername();
    }

    @GetMapping("/send")
    public String sendTestMessage(@RequestParam String message) {
        try {
            bot.sendNotification(message);
            return "Message sent successfully!";
        } catch (Exception e) {
            return "Failed to send message: " + e.getMessage();
        }
    }
}