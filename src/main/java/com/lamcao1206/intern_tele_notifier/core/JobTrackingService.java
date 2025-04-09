package com.lamcao1206.intern_tele_notifier.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.time.Duration;

@Slf4j
@Service
public class JobTrackingService {

    @Autowired
    private TelegramNotifierBot bot;

    @Value("${target.url}")
    private String targetUrl;

    private int previousLogoBoxCount = -1;
    private final WebDriver driver;

    public JobTrackingService() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); 
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        this.driver = new ChromeDriver(options);
    }

    @Scheduled(fixedRate = 30000) 
    public void checkDomChanges() {
        try {
            driver.get(targetUrl);
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.logo-box")));
            
            int currentLogoBoxCount = driver.findElements(By.cssSelector("div.logo-box")).size();
            
            if (previousLogoBoxCount == -1) {
                String initialMessage = "Initial load: Found " + currentLogoBoxCount + " job entries (logo-box elements) on " + targetUrl;
                bot.sendNotification(initialMessage);
                log.info(initialMessage);
            } else if (currentLogoBoxCount != previousLogoBoxCount) {
                String changeMessage = "Job entries changed on " + targetUrl + "!\n" +
                        "Previous count: " + previousLogoBoxCount + "\n" +
                        "Current count: " + currentLogoBoxCount + "\n" +
                        (currentLogoBoxCount > previousLogoBoxCount ?
                                "Added: " + (currentLogoBoxCount - previousLogoBoxCount) + " new job(s)" :
                                "Removed: " + (previousLogoBoxCount - currentLogoBoxCount) + " job(s)");
                bot.sendNotification(changeMessage);
                log.info(changeMessage);
            } else {
                log.debug("No change in job entries: {} logo-box elements", currentLogoBoxCount);
            }
            
            previousLogoBoxCount = currentLogoBoxCount;

        } catch (Exception e) {
            String errorMessage = "Error monitoring " + targetUrl + ": " + e.getMessage();
            bot.sendNotification(errorMessage);
            log.error(errorMessage, e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }
}