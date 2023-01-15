package com.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableScheduling
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            MyTelegramBot myTelegramBot=new MyTelegramBot();
            ComponentContainer.MY_TELEGRAM_BOT=myTelegramBot;
            telegramBotsApi.registerBot(myTelegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}