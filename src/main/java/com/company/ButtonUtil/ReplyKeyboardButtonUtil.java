package com.company.ButtonUtil;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ReplyKeyboardButtonUtil {
    public static KeyboardButton button(String text) {
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText(text);
        return keyboardButton;
    }

    public static KeyboardRow keyboardRow1(KeyboardButton button1) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(button1);
        return keyboardRow;
    }

    public static KeyboardRow keyboardRow2(KeyboardButton button1, KeyboardButton button2) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(button1);
        keyboardRow.add(button2);
        return keyboardRow;
    }

    public static List<KeyboardRow> row(KeyboardRow... buttons) {
        return new LinkedList<>(Arrays.asList(buttons));
    }

    public static ReplyKeyboardMarkup replyKeyboardMarkup(KeyboardRow... keyboardRows) {
        List<KeyboardRow> rows = ReplyKeyboardButtonUtil.row(keyboardRows);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rows);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup startMarkup() {
        KeyboardButton keyboardButton1 = ReplyKeyboardButtonUtil.button("Menu");
        KeyboardRow keyboardRow1 = ReplyKeyboardButtonUtil.keyboardRow1(keyboardButton1);
        return ReplyKeyboardButtonUtil.replyKeyboardMarkup(keyboardRow1);
    }

    public static ReplyKeyboardMarkup menuMarkup(String link) {
        KeyboardButton keyboardButton1 = ReplyKeyboardButtonUtil.button("Mening guruhlarim");
        KeyboardButton keyboardButton2 = ReplyKeyboardButtonUtil.button(link);
        KeyboardButton keyboardButton3 = ReplyKeyboardButtonUtil.button("⚙Sozlamalar");

        KeyboardRow keyboardRow1 = ReplyKeyboardButtonUtil.keyboardRow2(keyboardButton1, keyboardButton2);
        KeyboardRow keyboardRow2 = ReplyKeyboardButtonUtil.keyboardRow1(keyboardButton3);

        return ReplyKeyboardButtonUtil.replyKeyboardMarkup(keyboardRow1, keyboardRow2);
    }

    public static ReplyKeyboardMarkup settingsMarkup(){
        KeyboardButton keyboardButton1 = ReplyKeyboardButtonUtil.button("Linkni o'zgartirsh🔁");
        KeyboardButton keyboardButton2 = ReplyKeyboardButtonUtil.button("Xodimlarga CB biriktirish");
        KeyboardButton keyboardButton3 = ReplyKeyboardButtonUtil.button("Fileni guruhlarga tashlash uchun vaqt kiritish⌛");
        KeyboardButton keyboardButton4 = ReplyKeyboardButtonUtil.button("🔙Orqaga");

        KeyboardRow keyboardRow1 = ReplyKeyboardButtonUtil.keyboardRow2(keyboardButton1, keyboardButton2);
        KeyboardRow keyboardRow2 = ReplyKeyboardButtonUtil.keyboardRow1(keyboardButton3);
        KeyboardRow keyboardRow3 = ReplyKeyboardButtonUtil.keyboardRow1(keyboardButton4);

        return ReplyKeyboardButtonUtil.replyKeyboardMarkup(keyboardRow1, keyboardRow2, keyboardRow3);
    }

}
