package com.company;

import com.company.entity.Group;
import com.company.entity.Users;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static java.lang.Integer.parseInt;

public class MyTelegramBot extends TelegramLongPollingBot {

    private MainController mainController = new MainController();

    @Override
    public String getBotUsername() {
        return "vitrinasms_bot";
    }

    @Override
    public String getBotToken() {
        return "5698989429:AAGdcImYEgIApgnwOcDvNgUzeuzDGZeSfW8";
    }


    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);
        try {
            if (update.hasMessage()) {
                System.out.println(update);
                if (!update.getMessage().getNewChatMembers().isEmpty()) {
                    Users users = mainController.addUsers(update.getMessage().getFrom().getId());
                    for (User newChatMember : update.getMessage().getNewChatMembers()) {
                        if (newChatMember.getUserName().equals("vitrinasms_bot")) {
                            Group group = new Group();
                            group.setTitle(update.getMessage().getChat().getTitle());

                            group.setChatId(update.getMessage().getChat().getId());

                            users.addAllGroupList(group);
                            System.out.println(users.getAllGroupList());
                            System.out.println(newChatMember.getFirstName());
                            System.out.println(newChatMember.getUserName());
                        }
                    }
                    mainController.showGroupToUser(users);
                }
                if (update.getMessage().getLeftChatMember() != null) {
                    if (update.getMessage().getLeftChatMember().getUserName().equals("vitrinasms_bot")) {
                        Users userById = mainController.getUserById(update.getMessage().getFrom().getId());
                        /*if (update.getMessage().getChat().getUserName() != null) {
                            System.out.println("username " + update.getMessage().getChat().getUserName());
                            userById.deleteUsersGroupByUserName(update.getMessage().getChat().getUserName(), null);
                        }else {*/
                        System.out.println("chat id " + update.getMessage().getChat().getId());
                        userById.deleteUsersGroupByChatId(update.getMessage().getChat().getId());
//                        }
                        System.out.println(update.getMessage().getChat().getTitle());
                        System.out.println(update.getMessage().getChat().getUserName());
                        System.out.println(userById.getAllGroupList());
                    }
                }
                Message message = update.getMessage();
                if (message.hasText()) {
                    mainController.handleText(message);

                }
            } else if (update.hasCallbackQuery()) {
                mainController.handleCallbackQuery(update.getCallbackQuery());
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    //downlad file from telegram
   /* public void downloadFileFromTelegram(Document document, User user) {
        File file = getFilePath(document);

       *//* String localFilePath = "src/main/resources/" + document.getFileName();*//*
        String localFilePath = "src/main/resources/vitrina.xlsx";

        mainController.createXlsxFile1(localFilePath);

        java.io.File localFile = new java.io.File(localFilePath);
        InputStream is;
        try {
            is = new URL(file.getFileUrl("5698989429:AAGdcImYEgIApgnwOcDvNgUzeuzDGZeSfW8")).openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            FileUtils.copyInputStreamToFile(is, localFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        *//*readExcelFile("src/main/resources/vitrina.xlsx"*//**//*, user*//**//*);*//*
    }


    public File getFilePath(Document document) {
        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        File file;
        try {
            file = execute(getFile);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
*/
    public void sendMsg(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendEtitMsg(EditMessageText editMessageText) {
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendDoc(SendDocument sendDocument) {
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMessage(DeleteMessage deleteMessage) {
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPhoto(SendPhoto sendPhoto) {
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
