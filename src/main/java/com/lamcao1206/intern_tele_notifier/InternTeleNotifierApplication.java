package com.lamcao1206.intern_tele_notifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InternTeleNotifierApplication {
	public static void main(String[] args) {
		SpringApplication.run(InternTeleNotifierApplication.class, args);
	}
}
