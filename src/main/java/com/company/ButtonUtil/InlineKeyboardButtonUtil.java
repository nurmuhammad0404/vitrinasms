package com.company.ButtonUtil;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InlineKeyboardButtonUtil {
    public static InlineKeyboardButton button(String text, String callBack) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callBack);
        return button;
    }

    public static List<InlineKeyboardButton> row(InlineKeyboardButton... buttons) {
        return new LinkedList<>(Arrays.asList(buttons));
    }

    public static List<List<InlineKeyboardButton>> rowList(List<InlineKeyboardButton>... rows) {
        return new LinkedList<>(Arrays.asList(rows));
    }

    public static InlineKeyboardMarkup keyboard(List<List<InlineKeyboardButton>> rows) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup checkDownloadFile() {
        InlineKeyboardButton button1 = button("❌yo'q", "downloadFileNotApproved");
        InlineKeyboardButton button2 = button("✅ha", "downloadFileApproved");
        List<InlineKeyboardButton> row1 = row(button1, button2);
        List<List<InlineKeyboardButton>> rowList = rowList(row1);
        return keyboard(rowList);
    }

    public static InlineKeyboardMarkup checkFilterFile() {
        InlineKeyboardButton button1 = button("❌yo'q", "filterFileNotApproved");
        InlineKeyboardButton button2 = button("✅ha", "filterFileApproved");
        List<InlineKeyboardButton> row1 = row(button1, button2);
        List<List<InlineKeyboardButton>> rowList = rowList(row1);
        return keyboard(rowList);
    }

    public static InlineKeyboardMarkup checkSend(){
        InlineKeyboardButton button1 = button("❌yo'q", "sendingNotApproved");
        InlineKeyboardButton button2 = button("✅ha", "sendingApproved");
        List<InlineKeyboardButton> row1 = row(button1, button2);
        List<List<InlineKeyboardButton>> rowList = rowList(row1);
        return keyboard(rowList);
    }

    public static InlineKeyboardMarkup checkSendFileToGroup(){
        InlineKeyboardButton button1 = button("❌yo'q", "sendFileToGroupNotApproved");
        InlineKeyboardButton button2 = button("✅ha", "sendFileToGroupApproved");
        List<InlineKeyboardButton> row1 = row(button1, button2);
        List<List<InlineKeyboardButton>> rowList = rowList(row1);
        return keyboard(rowList);
    }

    public static InlineKeyboardMarkup addComment() {
        InlineKeyboardButton button1 = button("❌yo'q", "notAddComment");
        InlineKeyboardButton button2 = button("✅ha", "addComment");
        List<InlineKeyboardButton> row1 = row(button1, button2);
        List<List<InlineKeyboardButton>> rowList = rowList(row1);
        return keyboard(rowList);
    }

    public static InlineKeyboardMarkup sendFile() {
        InlineKeyboardButton button1 = button("commentni o'zgartirish✍", "editComment");
        InlineKeyboardButton button2 = button("guruhlarga jo'natish📤", "sendGroups");
        List<InlineKeyboardButton> row1 = row(button1);
        List<InlineKeyboardButton> row2 = row(button2);
        List<List<InlineKeyboardButton>> rowList = rowList(row1, row2);
        return keyboard(rowList);
    }

    public static InlineKeyboardMarkup createInlineKeyboardMurkub2(List<String> buttonTextList, String desc, int columnCount) {
        List<InlineKeyboardButton> row = new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        for (int i = 0; i < buttonTextList.size(); i++) {
            InlineKeyboardButton button = button(buttonTextList.get(i), buttonTextList.get(i) + desc);
            row.add(button);
            if (row.size() == columnCount || i == buttonTextList.size() - 1) {
                rowList.add(row);
                row = new LinkedList<>();
            }
        }
        return keyboard(rowList);
    }

    public static InlineKeyboardMarkup addBotToGroup(){
        InlineKeyboardButton button1 = button("➕botni guruhga qo'shish➕", "addBotToGroup");
        button1.setUrl("https://t.me/vitrinasms_bot?startgroup=1");
        List<InlineKeyboardButton> row1 = row(button1);
        List<List<InlineKeyboardButton>> rowList = rowList(row1);
        return keyboard(rowList);
    }

    public static InlineKeyboardMarkup approveSendFilteredFile(){
        InlineKeyboardButton button1 = button("Tasdiqlayman✔", "approve");
        List<InlineKeyboardButton> row1 = row(button1);
        List<List<InlineKeyboardButton>> rowList = rowList(row1);
        return keyboard(rowList);
    }

    public static InlineKeyboardMarkup editTime(){
        InlineKeyboardButton button1 = button("vaqtni o'zgartirish🔁", "etiteTime");
        List<InlineKeyboardButton> row1 = row(button1);
        List<List<InlineKeyboardButton>> rowList = rowList(row1);
        return keyboard(rowList);
    }

    public static InlineKeyboardMarkup createInlineKeyboardMurkub1(List<String> buttonTextList, List<String> desc, int columnCount) {
        List<InlineKeyboardButton> row = new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        for (int i = 0; i < buttonTextList.size(); i++) {
            InlineKeyboardButton button1 = button(buttonTextList.get(i), desc.get(i));
            row.add(button1);
            if (row.size() == columnCount || i == buttonTextList.size() - 1) {
                rowList.add(row);
                row = new LinkedList<>();
            }
        }
        InlineKeyboardButton button1 = button("➕botni guruhga qo'shish➕", "addBotToGroup");
        button1.setUrl("https://t.me/vitrinasms_bot?startgroup=1");
        row.add(button1);
        rowList.add(row);
        return keyboard(rowList);
    }

    public static InlineKeyboardMarkup createInlineKeyboardMurkub2(List<String> buttonTextList, List<String> desc, int columnCount) {
        List<InlineKeyboardButton> row = new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        for (int i = 0; i < buttonTextList.size(); i++) {
            InlineKeyboardButton button1 = button(buttonTextList.get(i), desc.get(i));
            InlineKeyboardButton button2 = button("jo'natish📤", "send" + desc.get(i));
            row.add(button1);
            row.add(button2);
            if (row.size() == columnCount || i == buttonTextList.size() - 1) {
                rowList.add(row);
                row = new LinkedList<>();
            }
        }
        return keyboard(rowList);
    }
}
