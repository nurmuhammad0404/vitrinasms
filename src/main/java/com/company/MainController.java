package com.company;

import com.company.ButtonUtil.InlineKeyboardButtonUtil;
import com.company.ButtonUtil.ReplyKeyboardButtonUtil;
import com.company.entity.CBAndEmployee;
import com.company.entity.Group;
import com.company.entity.Users;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import com.spire.xls.core.spreadsheet.autofilter.FilterOperatorType;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

import static java.lang.Integer.parseInt;


@Service
public class MainController {

    public void handleText(Message message) {
        String text = message.getText();
        String sendText;
        User user = message.getFrom();

        SendMessage sendMessage = new SendMessage();
        Long chatId = user.getId();
        sendMessage.setChatId(chatId);
        Users users = addUsers(chatId);
        if (text.equals("/start")) {
//            mergeFile(users);
            sendText = """
                    <b>Xush kelibsiz!!!
                                        
                    Bu bot - siz tashlagan linkdagi excel fileni siz istagan ko'rsatgich bo'yicha ustularni filterlab, siz istagan gruppaga tashlab beradi
                                        
                    ❗Guruhlarga tashlab berishi uchun avval Mening guruhlarim bo'limiga o'tib guruhlaringizni qo'shishingiz kerak</b>""";
            sendMsg(users, sendText, "HTML", ReplyKeyboardButtonUtil.startMarkup(), false);
            createNewDirectory(chatId.toString());
        } else if (text.startsWith("https:")) {
            users.setLink(text);
            sendMsg(users, "Link qabul qilindi", null, ReplyKeyboardButtonUtil.menuMarkup("Eski link bilan davom etish"), false);
            downloadFile(message, text);
        } else if (text.equals("Menu")) {
            if (users.getLink() == null) {
                sendMsg(users, "Quyidagilardan birini tanlang", null, ReplyKeyboardButtonUtil.menuMarkup("Linkni jo'natish"), false);
            } else {
                sendMsg(users, "Quyidagilardan birini tanlang", null, ReplyKeyboardButtonUtil.menuMarkup("Eski link bilan davom etish"), false);
            }
        } else if (text.equals("Yangi link jo'natish")) {
            sendMsg(users, "Linkni jo'nating🔗", null, null, false);
        } else if (text.equals("Linkni jo'natish")) {
            sendMsg(users, "Linkni jo'nating🔗", null, null, false);
        } else if (text.equals("Linkni o'zgartirsh\uD83D\uDD01")) {
            sendMsg(users, "Linkni jo'nating🔗", null, null, false);
        } else if (text.equals("Eski link bilan davom etish")) {
            if (users.getLink() == null) {
                sendMsg(users, "Sizda link mavjud emas❗", null, null, false);
            } else {
                downloadFile(message, users.getLink());
            }
        } else if (text.equals("⚙Sozlamalar")) {
            sendMsg(users, "Quyidagilardan birini tanlang", null, ReplyKeyboardButtonUtil.settingsMarkup(), false);
        } else if (text.equals("Mening guruhlarim")) {
            if (users.getAllGroupList() == null || users.getAllGroupList().isEmpty()) {
                sendMsg(users, "Sizda guruhlar mavjud emas❗", null, InlineKeyboardButtonUtil.addBotToGroup(), true);
            } else {
                showGroupToUser(users);
            }
        } else if (text.equals("/start@vitrinasms_bot 1")) {
            Group group = new Group();
            group.setTitle(message.getChat().getTitle());
            group.setChatId(message.getChat().getId());
            users.addAllGroupList(group);
            showGroupToUser(users);
        } else if (text.equals("Fileni guruhlarga tashlash uchun vaqt kiritish⌛")) {
            sendMsg(users, """
                    Vaqtni kiriting
                    <em>Misol: "06:00"</em>
                     
                    ⚠️Vaqtni faqat minutlarsiz kiritishingiz mumkin""", "HTML", null, false);
        } else if (text.equals("🔙Orqaga")) {
            sendMsg(users, users.getReplyMurkubText(), null, users.getLastReplyKeyboard(), false);
        } else if (text.equals("Xodimlarga CB biriktirish")) {
            sendMsg(users, """
                    Xodimlar va CBlarni chiziqcha bilan yozing!!!
                    <em>Misol: "xodim1 - CB1, xodim2 - CB2,..."</em>
                                       
                    Agar bitta CB ga bir nechta xodim biriktirmoqchi bo'lsangiz\040
                    <em>"xodim1 - CB1, CB2, xodim2 - CB3, CB4..."</em> ko'rinishida yozinig""", "HTML", null, false);
        } else if (users.getNewSendText().contains("Xodimlar va CBlarni chiziqcha bilan yozing")) {
            System.out.println("sv and employee " + text);
            List<String> CBEmployee = new LinkedList<>();
            if (text.contains("-")) {
                String string = text.trim();
                if (string.split("-").length == 1 || string.split("-")[0].equals("")) {
                    sendMsg(users, "Ma'lumot to'liq kiritilmadi❗❗❗", null, null, false);
                    sendMsg(users, """
                            Xodimlar va CBlarni chiziqcha bilan yozing!!!
                            <em>Misol: "xodim1 - CB1, xodim2 - CB2,..."</em>
                                               
                            Agar bitta CB ga bir nechta xodim biriktirmoqchi bo'lsangiz\040
                            <em>"xodim1 - CB1, CB2, xodim2 - CB3, CB4..."</em> ko'rinishida yozinig""", "HTML", null, false);
                } else {
                    if (!text.contains(",")) {
                        CBEmployee.add(text);
                    } else {
                        int index = -1;
                        int j = -1;
                        for (int i = 0; i < string.toCharArray().length; i++) {
                            if (string.toCharArray()[i] == ',') {
                                index = i;
                            }
                            if (string.toCharArray()[i] == '-' && index != -1) {
                                if (j == -1) {
                                    CBEmployee.add(string.substring(0, index));
                                } else {
                                    CBEmployee.add(string.substring(j + 1, index));
                                }
                                j = index;
                            } else if (i == string.toCharArray().length - 1) {
                                CBEmployee.add(string.substring(j + 1, i + 1));
                            }

                        }
                    }
                    for (int i = 0; i < CBEmployee.size(); i++) {
                        System.out.println(i + " " + CBEmployee.get(i));
                        CBAndEmployee cbAndEmployee = new CBAndEmployee();
                        cbAndEmployee.setEmployee(CBEmployee.get(i).split("-")[0].trim());
                        List<String> CBList = new LinkedList<>();
                        if (CBEmployee.get(i).contains(",")) {
                            String[] split = CBEmployee.get(i).split("-")[1].trim().split(",");
                            for (String s : split) {
                                CBList.add(s.trim());
                            }
                        } else {
                            CBList.add(CBEmployee.get(i).split("-")[1].trim());
                        }
                        cbAndEmployee.setCBList(CBList);
                        users.addCBAndEmployee(cbAndEmployee);
                        System.out.println(users.getCbAndEmployees());
                    }

                   /* for (CBAndEmployee cbAndEmployee : users.getCbAndEmployees()) {
                        System.out.println(cbAndEmployee);
                    }*/
                }
                sendMsg(users, "Qabul qilindi", null, null, false);
            } else {
                sendMsg(users, "Noto'g'ri formatda kiritildi❗❗❗", null, null, false);
                sendMsg(users, """
                        Xodimlar va CBlarni chiziqcha bilan yozing!!!
                        <em>Misol: "xodim1 - CB1, xodim2 - CB2,..."</em>
                                           
                        Agar bitta CB ga bir nechta xodim biriktirmoqchi bo'lsangiz\040
                        <em>"xodim1 - CB1, CB2, xodim2 - CB3, CB4..."</em> ko'rinishida yozinig""", "HTML", null, false);
            }
        } else if (users.getNewSendText().contains("Vaqtni kiriting")) {
            if (text.contains(":")) {
                String[] time = text.split(":");
                if (time[1].equals("00")) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                        LocalTime localTime = LocalTime.parse(time[0] + ":" + time[1], formatter);
                        users.setSendFileToGroupTime(localTime);
                        sendMsg(users, "Fileni guruhlarga jo'natish vaqti ⌛<em>" + users.getSendFileToGroupTime() + "</em> ga belgilandi", "HTML", InlineKeyboardButtonUtil.editTime(), true);
                    } catch (DateTimeParseException e) {
                        sendMsg(users, "Vaqt noto'gri formatda kiritildi❗❗❗ ", null, null, false);
                        sendMsg(users, """
                                Vaqtni kiriting      
                                <em>Misol: "06:00"</em>
                                 
                                ⚠️Vaqtni faqat minutlarsiz kiritishingiz mumkin""", "HTML", null, false);
                    }
                } else {
                    sendMsg(users, "Vaqt noto'gri formatda kiritildi❗❗❗ ", null, null, false);
                    sendMsg(users, """
                            Vaqtni kiriting      
                            <em>Misol: "06:00"</em>
                             
                            ⚠️Vaqtni faqat minutlarsiz kiritishingiz mumkin""", "HTML", null, false);
                }
            } else {
                sendMsg(users, "Vaqt noto'gri formatda kiritildi❗❗❗", null, null, false);
                sendMsg(users, """
                        Vaqtni kiriting
                        <em>Misol: "06:00"</em>
                                            
                        ⚠️Vaqtni faqat minutlarsiz kiritishingiz mumkin""", "HTML", null, false);
            }
        } else if (users.getNewSendText().equals("Qaysi ustunlar bo'yicha filter qilmoqchisiz?\n" +
                "ustunlar nomini vergul bilan yozib jo'nating")) {
            if (users.getColumnList() != null) {
                List<String> columnList = new LinkedList<>();
                users.setColumnList(columnList);
            }
            if (users.getColumnsLater() != null) {
                Map<String, List<String>> columnsLater = new LinkedHashMap<>();
                users.setColumnsLater(columnsLater);
            }
            if (text.contains(",")) {
                String[] columns = text.split(",");
                List<String> columnList = new LinkedList<>(Arrays.asList(columns));
                System.out.println(columnList);
                users.setColumnList(columnList);
                users.setColumnCount(0);
            } else {
                List<String> columnList = new LinkedList<>();
                columnList.add(text);
                System.out.println(columnList);
                users.setColumnList(columnList);
                users.setColumnCount(0);
            }
            String column = users.getColumnList().get(users.getColumnCount());
            users.setColumnCount(users.getColumnCount() + 1);
            sendText = column + " ustunidan qaysi so'zlar bo'ycha filter qilmoqchisz?\n" +
                    "So'zlarni vergul bilan yozib jo'nating";
            sendMsg(users, sendText, null, null, false);
        } else if (users.getNewSendText().contains("ustunidan qaysi so'zlar bo'ycha filter qilmoqchisz?") &&
                users.getColumnList().size() >= users.getColumnCount()) {
            if (text.contains(", ")) {
                System.out.println(text);
                String[] columnsLaterArray = text.split(", ");
                List<String> columnsLaterList = new LinkedList<>(Arrays.asList(columnsLaterArray));
                users.addColumnsLater(users.getColumnList().get(users.getColumnCount() - 1), columnsLaterList);
            } else {
                System.out.println(text);
                List<String> columnsLaterList = new LinkedList<>();
                columnsLaterList.add(text);
                users.addColumnsLater(users.getColumnList().get(users.getColumnCount() - 1), columnsLaterList);
            }
            if (users.getColumnList().size() != users.getColumnCount()) {
                String column = users.getColumnList().get(users.getColumnCount());
                sendText = column + " ustunidan qaysi so'zlar bo'ycha filter qilmoqchisz?\n" +
                        "So'zlarni vergul bilan yozib jo'nating";
                sendMsg(users, sendText, null, null, false);
            } else {
                sendMsg(users, "Jarayon boshlandi", null, null, false);
                filterXlsxFile(users);
            }
            users.setColumnCount(users.getColumnCount() + 1);
        } else if (users.getReplyMurkubText().equals("Sizda guruhlar mavjud emas. File yuborilishi uchun guruhlaringizni qo'shing❗")) {
            showSendGroupToUser(users);
        } else if (users.getReplyMurkubText().equals("File qaysi guruhlarga yuborilsin? Sizda quyidagi guruhlar mavjud:")) {
            showSendGroupToUser(users);
        }
    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        System.out.println(callbackData);
        System.out.println(callbackQuery.getMessage().getText());
        Long chatId = callbackQuery.getFrom().getId();
        String sendText;
        Users users = getUserById(chatId);
        if (callbackData.equals("downloadFileApproved")) {
            List<String> sheetNameList = getAllSheetFromFile(ComponentContainer.excelFileUrl + users.getChatId() + ComponentContainer.copyFileName);
            sheetNameList.add("🔙Orqaga");
            InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardButtonUtil.createInlineKeyboardMurkub2(sheetNameList, "sheet", 2);
            sendMsg(users, "Qaysi sheet(лист) dagi jadvalni filterlamoqchisz?", null, inlineKeyboardMarkup, true);
        } else if (callbackData.equals("downloadFileNotApproved")) {
            deleteMsg(chatId, callbackQuery.getMessage().getMessageId());
            sendText = "Boshqa linkni jo'nating";
            sendMsg(users, sendText, null, null, false);
        } else if (callbackData.contains("🔙Orqaga")) {
            deleteMsg(chatId, callbackQuery.getMessage().getMessageId());
        } else if (callbackData.contains("sheet") && !callbackData.startsWith("🔙Orqaga")) {
            users.setSheetName(callbackData.substring(0, callbackData.length() - 5));
            System.out.println(users.getSheetName());
            sendText = "Qaysi ustunlar bo'yicha filter qilmoqchisiz?\n" +
                    "ustunlar nomini vergul bilan yozib jo'nating";
            sendMsg(users, sendText, null, null, false);
        } else if (callbackData.equals("filterFileApproved")) {
            sendMsg(users, "Fileni guruhlarga yuborilishini tasdiqlaysizmi?", null, InlineKeyboardButtonUtil.approveSendFilteredFile(), true);
        } else if (callbackData.equals("filterFileNotApproved")) {
            deleteMsg(chatId, callbackQuery.getMessage().getMessageId());
            sendText = "Qaysi ustunlar bo'yicha filter qilmoqchisiz?\n" +
                    "ustunlar nomini vergul bilan yozib jo'nating";
            sendMsg(users, sendText, null, null, false);
        } else if (callbackData.equals("sendingApproved")) {
            if (users.getAllGroupList() != null) {
                showSendGroupToUser(users);
            } else {
                sendMsg(users, "❗Sizda guruhlar mavjud emas. \n" +
                        "Mening guruhlarim bo'limiga o'tib guruhlaringizni qo'shishingiz kerak", null, null, false);
            }
        } else if (callbackData.equals("approve")) {
            users.setApproveSendFileToGroups(true);
            sendMsg(users, "File guruhlarga ручной yuborishni xohlaysizmi?", null, InlineKeyboardButtonUtil.checkSend(), true);
        } else if (callbackData.equals("sendingNotApproved")) {
            deleteMsg(users.getChatId(), callbackQuery.getMessage().getMessageId());
            sendMsg(users, """
                    Fileni guruhlarga jo'natish uchun vaqt kiritdingizmi?
                                        
                    Agar kiritmagan bo'lsangiz, <em>"⚙Sozlamalar"</em> bo'limidan
                    <em>"Fileni guruhlarga tashlash uchun vaqt kiritish⌛"</em> buyrug'ini tanlash orqali kiritishingiz mumkin""", "HTML", null, false);
        } else if (callbackData.equals("etiteTime")) {
            sendMsg(users, """
                    Vaqtni kiriting      
                    <em>Misol: "06:00"</em>
                     
                    ⚠️Vaqtni faqat minutlarsiz kiritishingiz mumkin""", "HTML", null, false);
        }
        /*else if (callbackData.equals("addComment")) {
            sendText = "Commentingizni yozib jo'nating";
            sendMsg(users, sendText, null, null, false);
        } else if (callbackData.equals("notAddComment")) {
            sendDocument(users, "/filteredFile.xlsx", null, InlineKeyboardButtonUtil.sendFile(), null);
        } else if (callbackData.equals("sendGroups")) {
            if (users.getAllGroupList() != null) {
                showSendGroupToUser(users);
            } else {
                sendMsg(users, "❗Sizda guruhlar mavjud emas. \n" +
                        "Mening guruhlarim bo'limiga o'tib guruhlaringizni qo'shishingiz kerak", null, null, false);
            }
        }*/
        else if (callbackData.contains("send")) {
            String userName = callbackData.substring(4);
            sendDocumentToGroup(userName, null, users.getChatId(), users.getComment());
        } /*else if (callbackData.equals("editComment")) {
            sendMsg(users, "Commentingizni yozib jo'nating", null, null, false);
        }*/
    }

    public void filterXlsxFile(Users users) {
        System.out.println("filter");
        String filePath = ComponentContainer.excelFileUrl + users.getChatId() + "/downloadedFile.xlsx";
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        XSSFSheet sheet = workbook.getSheet(users.getSheetName());

        int rowNum = -1;
        Map<Integer, List<String>> colIdMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> pair : users.getColumnsLater().entrySet()) {
            rowNum = getColId(filePath, users.getSheetName(), pair.getKey().trim())[0];
            int colId = getColId(filePath, users.getSheetName(), pair.getKey().trim())[1];
            if (colId != -1) {
                colIdMap.put(colId, pair.getValue());
            }
        }
        if (rowNum != -1) {
            int firstCell = sheet.getRow(rowNum).getFirstCellNum();
            int lastCell = sheet.getRow(rowNum).getLastCellNum();

            CellAddress firstCellAdreess = sheet.getRow(rowNum).getCell(firstCell).getAddress();
            CellAddress lastCellAdreess = sheet.getRow(rowNum).getCell(lastCell - 1).getAddress();

            sheet.setAutoFilter(CellRangeAddress.valueOf(firstCellAdreess.toString() + ":" + lastCellAdreess.toString()));
        }

//            CTAutoFilter sheetFilter = sheet.getCTWorksheet().getAutoFilter();
//
//            sheet.getCTWorksheet().setAutoFilter(sheetFilter);


        try {
            int i = 0;
            for (Map.Entry<Integer, List<String>> pair : colIdMap.entrySet()) {
                setHiddenAllRows(users);
                XSSFWorkbook workbook1 = new XSSFWorkbook(filePath);

                XSSFSheet sheet1 = workbook1.getSheet(users.getSheetName());
                i++;
                boolean isDate = false;
                List<String> dateValues = new LinkedList<>();
                XSSFRow r1;
                for (String value : pair.getValue()) {
                    for (Row r : sheet1) {
                        for (Cell c : r) {
                            if (c.getColumnIndex() == pair.getKey()) {
                                if (c.getCellType().equals(CellType.STRING) && !value.trim().contains(c.getStringCellValue().trim())) {
                                    r1 = (XSSFRow) c.getRow();
                                    if (r1.getRowNum() != rowNum) {
                                        r1.getCTRow().setHidden(true);
                                    }
                                } else if (c.getCellType().equals(CellType.FORMULA) && !value.trim().contains(calculateAndGetValue(filePath,
                                        sheet1.getSheetName(), String.valueOf(c.getAddress())).toString().trim())) {
                                    r1 = (XSSFRow) c.getRow();
                                    if (r1.getRowNum() != rowNum) {
                                        r1.getCTRow().setHidden(true);
                                    }
                                } else if (c.getCellType().equals(CellType.NUMERIC)) {
                                    if (HSSFDateUtil.isCellDateFormatted(c)) {
//                                    StringBuilder value = new StringBuilder();
                                        LocalDate localDate;
                                        DataFormatter dataFormatter = new DataFormatter();
                                        dataFormatter.addFormat("m/d/yy", new java.text.SimpleDateFormat("M/d/yyyy"));
                                        String formattedCellStr = dataFormatter.formatCellValue(c);
                                        String[] date = formattedCellStr.trim().split("/");
                                        localDate = getDate(date);
                                        isDate = true;
                                        if (!pair.getValue().contains(formattedCellStr)) {
                                            r1 = (XSSFRow) c.getRow();
                                            if (r1.getRowNum() != rowNum) {
                                                r1.getCTRow().setHidden(true);
                                            }
                                        } else {
                                        /*String month = localDate.getMonth().toString();
                                        StringBuilder month1 = new StringBuilder();
                                        for (int j = 0; j < month.toCharArray().length; j++) {
                                           char c1;
                                           if (j == 0){
                                               c1 = month.charAt(j);
                                               month1.append(c1);
                                           }else {
                                               c1 = Character.toLowerCase(month.charAt(j));
                                               month1.append(c1);
                                           }
                                        }*/
//                                        value.append("\"").append(month1).append(" ")./*append(localDate.getDayOfMonth()).append(" ").*/append(localDate.getYear()).append("\"");
//                                        pair.getValue().add(.toString());

//                                        c.setCellValue(formattedCellStr);
                                            if (!dateValues.contains(formattedCellStr)) {
                                                dateValues.add(formattedCellStr);
                                            }
                                        }
                                    } else {
//                                        List<String> numericValue = new LinkedList<>();
//                                        for (String s : pair.getValue()) {
                                        String str = value.replaceAll("[^\\d.]", "");
//                                            numericValue.add(str);
//                                        }
                                        if (!str.contains(String.valueOf((int) c.getNumericCellValue()))) {
                                            r1 = (XSSFRow) c.getRow();
                                            if (r1.getRowNum() != rowNum) {
                                                r1.getCTRow().setHidden(true);
                                            }
                                        }
                                    }
                                } else if (c.getCellType().equals(CellType.BLANK)) {
                                    r1 = (XSSFRow) c.getRow();
                                    if (r1.getRowNum() != rowNum) {
                                        r1.getCTRow().setHidden(true);
                                    }
                                } else {
                                    if (r.getRowNum() != 0 && r.getCell(4) != null && r.getCell(4).getCellType().equals(CellType.STRING)) {
                                        users.addActiveCB(r.getCell(4).getStringCellValue());
                                    }
                                    users.addLastFilterRowNum(r.getRowNum());
                                }
                            }
                        }
                    }
                }
                /*if (!isDate) {
                    setFilterValue(sheetFilter, i, pair.getKey(), pair.getValue(), isDate);
                    i++;
                } else {
                    setFilterValue(sheetFilter, i, pair.getKey(), dateValues, isDate);
                }*/

                OutputStream outputStream = new FileOutputStream(ComponentContainer.excelFileUrl + users.getChatId() + "/filteredFile" + i + ".xlsx");
                workbook1.write(outputStream);
            }
            /*OutputStream outputStream = new FileOutputStream(ComponentContainer.excelFileUrl + users.getChatId() + "/filteredFile.xlsx");
            workbook.write(outputStream);*/

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mergeFile(users, rowNum);
        sendDocument(users, "/filteredFile.xlsx", null, InlineKeyboardButtonUtil.checkFilterFile(), "Filterlangan file to'g'rimi?");
        users.setApproveSendFileToGroups(false);
    }

    public void filterByCB(String sv, Users users, String employee) {
        String filePath = ComponentContainer.excelFileUrl + users.getChatId() + "/filteredFile.xlsx";
        /*List<String> svList = new LinkedList<>();
        for (CBAndEmployee cbAndEmployee : users.getCbAndEmployees()) {
            for (String s : cbAndEmployee.getCBList()) {
                if (!svList.contains(s)) {
                    svList.add(s);
                }
            }
        }*/
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(filePath);
            XSSFSheet sheet = workbook.getSheet(users.getSheetName());

            CTAutoFilter sheetFilter = sheet.getCTWorksheet().getAutoFilter();

            sheet.getCTWorksheet().setAutoFilter(sheetFilter);

            XSSFRow r1;
            for (Row row : sheet) {
                if (row.getRowNum() != 0) {
                    for (Cell cell : row) {
                        if (cell.getColumnIndex() == 4) {
                            if (cell.getCellType().equals(CellType.STRING) && !sv.trim().equals(cell.getStringCellValue())) {
                                r1 = (XSSFRow) cell.getRow();
                                r1.getCTRow().setHidden(true);
                            }
                        }
                    }
                }
            }
            OutputStream outputStream = new FileOutputStream(ComponentContainer.excelFileUrl + users.getChatId() + "/" + sv + ".xlsx");
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Group group : users.getAllGroupList()) {
            sendDocumentToGroup(group.getChatId().toString(), sv, users.getChatId(), employee);
        }

    }

    public void downloadFile(Message message, String link) {
        Long chatId = message.getChatId();
        Users users = getUserById(chatId);
        String expandedURL = expandUrl(link);
        String fileName;
        ComponentContainer.copyFileName = "/downloadedFile.xlsx";
        if (expandedURL != null) {
            String[] splitFileName = expandedURL.split("resid");
            fileName = splitFileName[1];
        } else {
            String[] splitFileName = link.split("resid");
            fileName = splitFileName[1];
        }
        System.out.println("download file");
        try {
            InputStream in2 = new URL("https://onedrive.live.com/download?resid" + fileName).openStream();
            Files.copy(in2, Paths.get(ComponentContainer.excelFileUrl + users.getChatId() + "/downloaded.xlsx"), StandardCopyOption.REPLACE_EXISTING);
            setHiddenAllRows(users);
            sendDocument(users, ComponentContainer.copyFileName, null, InlineKeyboardButtonUtil.checkDownloadFile(), "Siz filter qilmoqchi bo'lgan file shumi?");
        } catch (IOException e) {
            sendMsg(users, "Fileni yuklab olib bo'lmadi yoki bu linkda file mavjud emas", null, null, false);
            throw new RuntimeException(e);
        }
        System.out.println("download finished");
    }

    public void showGroupToUser(Users users) {
        List<String> groupNameList = new LinkedList<>();
        List<String> groupUserNameList = new LinkedList<>();
        for (Group group : users.getAllGroupList()) {
            groupNameList.add(group.getTitle());
            if (group.getChatId() != null) {
                groupUserNameList.add(group.getChatId().toString());
            }
        }
        InlineKeyboardMarkup inlineKeyboardMarkup =
                InlineKeyboardButtonUtil.createInlineKeyboardMurkub1(groupNameList, groupUserNameList, 2);
        sendMsg(users, "Sizda quyidagi guruhlar mavjud: ", null, inlineKeyboardMarkup, true);
    }

    private void showSendGroupToUser(Users users) {
        if (users.getAllGroupList() == null) {
            return;
        }
        List<String> groupNameList = new LinkedList<>();
        List<String> groupUserNameList = new LinkedList<>();
        for (Group group : users.getAllGroupList()) {
            groupNameList.add(group.getTitle());
            if (group.getChatId() != null) {
                groupUserNameList.add(group.getChatId().toString());
            }
        }
        InlineKeyboardMarkup inlineKeyboardMarkup =
                InlineKeyboardButtonUtil.createInlineKeyboardMurkub2(groupNameList, groupUserNameList, 2);
        sendMsg(users, "File qaysi guruhlarga yuborilsin? Sizda quyidagi guruhlar mavjud: ", null, inlineKeyboardMarkup, true);
    }

    public Object calculateAndGetValue(String filePath, String sheetName, String cellAdress) {
        Workbook workbook = new Workbook();
        workbook.loadFromFile(filePath);
        Worksheet worksheet = workbook.getWorksheets().get(sheetName);

        return worksheet.getCellRange(cellAdress).getFormulaValue();
    }

    public List<String> getAllSheetFromFile(String filePath) {
        List<String> sheetNameList = new LinkedList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(filePath);
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            while (sheetIterator.hasNext()) {
                Sheet next = sheetIterator.next();
                sheetNameList.add(next.getSheetName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sheetNameList;
    }

    public void contvertEcxelToImage(String sheetName, String ctFilterVal, int payCount) {
//        com.spire.xls.Workbook wb = new com.spire.xls.Workbook();
//        wb.loadFromFile(ComponentContainer.filterFileName + "_" + ctFilterVal + "_" + payCount + "X.xlsx");
//
//        Worksheet worksheet = wb.getWorksheets().get(sheetName);
//        worksheet.saveToImage(ComponentContainer.imagePath + "_" + ctFilterVal + "_" + payCount + "X.png");
        try {
                /*com.aspose.cells.Workbook workbook = new com.aspose.cells.Workbook(ComponentContainer.filterFileName + "_" + ctFilterVal + "_" + payCount + ".xlsx");

            ImageOrPrintOptions imgOptions = new ImageOrPrintOptions();

            imgOptions.setImageType(ImageType.PNG);

            imgOptions.setOnePagePerSheet(true);

            com.aspose.cells.Worksheet sheet = workbook.getWorksheets().get(sheetName);

            SheetRender sr = new SheetRender(sheet, imgOptions);
            for (int page = 0; page < sr.getPageCount(); page++) {
                // Generate an image for the worksheet
                sr.toImage(page, ComponentContainer.imagePath + "_" + ctFilterVal + "_" + payCount + ".png");
            }*/
//            sr.toImage(0,ComponentContainer.imagePath + "_" + ctFilterVal + "_" + payCount + ".png");
            Workbook workbook = new Workbook();
            workbook.loadFromFile(ComponentContainer.filterFileName + "_" + ctFilterVal + "_" + payCount + ".xlsx");

            Worksheet sheet = workbook.getWorksheets().get(sheetName);
            BufferedImage bufferedImage = sheet.toImage(1, 1, sheet.getLastRow() * 2, sheet.getLastColumn());
//            sheet.saveToImage(ComponentContainer.imagePath + "_" + ctFilterVal + "_" + payCount + ".png");
            ImageIO.write(bufferedImage, "PNG", new File(ComponentContainer.imagePath + "_" + ctFilterVal + "_" + payCount + ".png"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sendPhoto(ComponentContainer.imagePath + "_" + ctFilterVal + "_" + payCount + ".png", ctFilterVal + "_" + payCount);

    }

    public String expandUrl(String shortenedUrl) {
        URL url;
        String expandedURL;
        try {
            url = new URL(shortenedUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
            httpURLConnection.setInstanceFollowRedirects(false);
            expandedURL = httpURLConnection.getHeaderField("Location");
            httpURLConnection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return expandedURL;
    }

    public EditMessageText sendEtitMessageText(Long chatId, String text, Integer messageId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setText(text);
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        editMessageText.setMessageId(messageId);
        ComponentContainer.MY_TELEGRAM_BOT.sendEtitMsg(editMessageText);
        return editMessageText;
    }

    public void deleteMsg(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        ComponentContainer.MY_TELEGRAM_BOT.deleteMessage(deleteMessage);
    }

    public void sendMsg(Users users, String text, String parseMode, ReplyKeyboard replyKeyboardMarkup, boolean inline) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(users.getChatId());
        if (text != null) {
            sendMessage.setText(text);
            users.setLastSendText(users.getNewSendText());
            users.setNewSendText(text);
        }

        if (replyKeyboardMarkup != null) {
            if (!inline) {
                users.setReplyMurkubText(text);
                users.setLastReplyKeyboard(users.getNewReplyKeyboard());
                users.setNewReplyKeyboard(replyKeyboardMarkup);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
            } else {
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
            }
        }
        if (parseMode != null) {
            sendMessage.setParseMode(parseMode);
        }
        ComponentContainer.MY_TELEGRAM_BOT.sendMsg(sendMessage);
    }

    public void sendPhoto(String filePath, String desctiption) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId("-1001377386465");
        sendPhoto.setCaption(desctiption);
        File file = new File(filePath);
        sendPhoto.setPhoto(new InputFile(file));
        ComponentContainer.MY_TELEGRAM_BOT.sendPhoto(sendPhoto);
    }

    public void sendDocument(Users users, String fileName, ReplyKeyboardMarkup replyKeyboardMarkup, InlineKeyboardMarkup inlineKeyboardMarkup, String caption) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(users.getChatId());
        if (!fileName.contains(".xlsx")) {
            fileName = fileName + ".xlsx";
        }
        File file = new File(ComponentContainer.excelFileUrl + users.getChatId() + fileName);
        sendDocument.setDocument(new InputFile(file));
        if (replyKeyboardMarkup != null) {
            sendDocument.setReplyMarkup(replyKeyboardMarkup);
        }
        if (inlineKeyboardMarkup != null) {
            sendDocument.setReplyMarkup(inlineKeyboardMarkup);
        }
        if (caption != null) {
            sendDocument.setCaption(caption);
        }
        ComponentContainer.MY_TELEGRAM_BOT.sendDoc(sendDocument);
    }

    public void sendDocumentToGroup(String groupUserName, String fileName, Long chatId, String captian) {
        SendDocument sendDocument = new SendDocument();
        File file;
        if (fileName == null) {
            file = new File(ComponentContainer.excelFileUrl + chatId + "/filteredFile.xlsx");
        } else {
            file = new File(ComponentContainer.excelFileUrl + chatId + "/" + fileName + ".xlsx");
        }
        sendDocument.setDocument(new InputFile(file));
        sendDocument.setCaption(captian);
        sendDocument.setChatId(groupUserName);
        ComponentContainer.MY_TELEGRAM_BOT.sendDoc(sendDocument);

    }

    public List<String> getAllSv(String filePath, String sheetName) {
        List<String> svList = new LinkedList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(filePath);
            XSSFSheet sheet = workbook.getSheet(sheetName);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getColumnIndex() == 4 && row.getRowNum() > 0 && cell.getCellType().equals(CellType.STRING)) {
                        if (!svList.contains(cell.getStringCellValue()) && !cell.getStringCellValue().equals("?")) {
                            svList.add(cell.getStringCellValue());
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return svList;
    }

    private LocalDate getDate(String[] date) {
        int day = parseInt(date[1]);
        int month = parseInt(date[0]);
        int year = parseInt(date[2]);

        return LocalDate.of(year, month, day);
    }

    public Users getUserById(Long chatId) {
        for (Users users : ComponentContainer.usersList) {
            if (users.getChatId().equals(chatId)) {
                return users;
            }
        }
        return null;
    }

    public Users addUsers(Long chatId) {
        if (ComponentContainer.usersList == null) {
            ComponentContainer.usersList = new LinkedList<>();
        } else {
            for (Users users : ComponentContainer.usersList) {
                if (Objects.equals(users.getChatId(), chatId)) {
                    return users;
                }
            }
        }
        Users users = new Users();
        users.setChatId(chatId);
        ComponentContainer.usersList.add(users);
        return users;
    }

    public void createNewDirectory(String name) {
        File file = new File(ComponentContainer.excelFileUrl + "/" + name);
        try {
            FileUtils.forceMkdir(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int[] getColId(String filePath, String sheetName, String column) {
        int[] rowAndColId = new int[2];
        rowAndColId[0] = -1;
        rowAndColId[1] = -1;
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(filePath);
            XSSFSheet sheet = workbook.getSheet(sheetName);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType().equals(CellType.STRING)) {
                        if (cell.getStringCellValue().trim().equals(column)) {
                            rowAndColId[0] = row.getRowNum();
                            rowAndColId[1] = cell.getColumnIndex();
                            return rowAndColId;
                        }
                    } else if (cell.getCellType().equals(CellType.NUMERIC)) {
                        if (String.valueOf(((int) cell.getNumericCellValue())).trim().equals(column)) {
                            rowAndColId[0] = row.getRowNum();
                            rowAndColId[1] = cell.getColumnIndex();
                            return rowAndColId;
                        }
                    } else if (cell.getCellType().equals(CellType.FORMULA)) {
                        if (String.valueOf(cell.getCellFormula()).trim().equals(column)) {
                            rowAndColId[0] = row.getRowNum();
                            rowAndColId[1] = cell.getColumnIndex();
                            return rowAndColId;
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return rowAndColId;
    }

 /*   public void setFilterValue(CTAutoFilter sheetFilter, int i, int colId, List<String> values, boolean isDate) {
        for (String value : values) {
            System.out.println("value " + value);
        }

        CTFilterColumn ctFilterColumn = sheetFilter.insertNewFilterColumn(i);
        ctFilterColumn.setColId(colId);
        CTFilters ctFilters = ctFilterColumn.addNewFilters();

        if (isDate) {
         int count = 0;
            for (String value : values) {
                String[] date = value.trim().split("/");
                ctFilters.setCalendarType(GREGORIAN);
                CTDateGroupItem ctDateGroupItem = ctFilters.addNewDateGroupItem();
                int day = Integer.parseInt(date[1]);
                int month = Integer.parseInt(date[0]);
                int year = Integer.parseInt(date[2]);
                ctDateGroupItem.setDay(day);
                ctDateGroupItem.setMonth(month);
                ctDateGroupItem.setYear(year);
                ctDateGroupItems[count] = ctDateGroupItem;
                System.out.println(ctDateGroupItem);
                ctFilters.setDateGroupItemArray(count, ctDateGroupItem);
                ctFilters.setDateGroupItemArray(ctDateGroupItems);
                System.out.println(Arrays.toString(ctDateGroupItems));
                count++;
            }
            for (int j = 0; j < values.size(); j++) {
                CTFilter ctFilter = ctFilters.addNewFilter();
                ctFilter.setVal(values.get(0));

                ctFilters.setFilterArray(j, ctFilter);
            }
        } else {
            for (int j = 0; j < values.size(); j++) {
                CTFilter ctFilter = ctFilters.addNewFilter();
                ctFilter.setVal(values.get(j));
                ctFilters.setFilterArray(j, ctFilter);
            }
        }
    }*/

    public void setHiddenAllRows(Users users) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(ComponentContainer.excelFileUrl + users.getChatId() + "/downloaded.xlsx");
            XSSFSheet sheet = workbook.getSheet("Витирина");
            sheet.getCTWorksheet().unsetAutoFilter();
            XSSFRow r;
            for (Row row : sheet) {

                r = ((XSSFRow) row);
                r.getCTRow().setHidden(false);
            }
            OutputStream outputStream = new FileOutputStream(ComponentContainer.excelFileUrl + users.getChatId() + "/downloadedFile.xlsx");
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void mergeFile(Users users, int notHiddenRow) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(ComponentContainer.excelFileUrl + users.getChatId() + "/downloadedFile.xlsx");
            XSSFSheet sheet = workbook.getSheet(users.getSheetName());
            XSSFRow r;
            for (Row row : sheet) {
                if (!users.getLastFilterRowNumList().contains(row.getRowNum())) {
                    if (row.getRowNum() != notHiddenRow) {
                        r = ((XSSFRow) row);
                        r.getCTRow().setHidden(true);
                    }
                }
            }
            OutputStream outputStream = new FileOutputStream(ComponentContainer.excelFileUrl + users.getChatId() + "/filteredFile.xlsx");
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*try {
            // excel files
            FileInputStream excellFile1 = new FileInputStream(ComponentContainer.excelFileUrl + "5109454141/downloadedFile.xlsx");
            FileInputStream excellFile2 = new FileInputStream(ComponentContainer.excelFileUrl + "5109454141/filteredFile.xlsx");

            // Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook1 = new XSSFWorkbook(excellFile1);
            XSSFWorkbook workbook2 = new XSSFWorkbook(excellFile2);
            // Get first/desired sheet from the workbook
            XSSFSheet sheet1 = workbook1.getSheet("Витирина");
            XSSFSheet sheet2 = workbook2.getSheet("Витирина");

            // add sheet2 to sheet1
            addSheet(sheet1, sheet2, users);
            excellFile1.close();

            // save merged file
            File mergedFile = new File(
                    ComponentContainer.excelFileUrl + "5109454141/mergeFile.xlsx");
            if (!mergedFile.exists()) {
                mergedFile.createNewFile();

            }
            FileOutputStream out = new FileOutputStream(mergedFile);
            workbook1.write(out);
            out.close();
            System.out.println("Files were merged succussfully");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void addSheet(XSSFSheet mergedSheet, XSSFSheet sheet, Users users) {
        // map for cell styles
        Map<Integer, XSSFCellStyle> styleMap = new HashMap<>();

        // This parameter is for appending sheet rows to mergedSheet in the end
        int len = mergedSheet.getLastRowNum();
//        int len = users.getLastFilterRowNumList().size();
        System.out.println(len);
//        for (int j = sheet.getFirstRowNum() + 1; j <= sheet.getLastRowNum(); j++) {
        for (int j = 0; j < users.getLastFilterRowNumList().size(); j++) {
            XSSFRow row;
            if (j == 0) {
                row = sheet.getRow(j);
            } else {
                row = sheet.getRow(users.getLastFilterRowNumList().get(j));
            }
            XSSFRow mrow = mergedSheet.createRow(len + j + 1);

            if (row != null) {
                for (int k = row.getFirstCellNum(); k < row.getLastCellNum(); k++) {
                    XSSFCell cell = row.getCell(k);
                    XSSFCell mcell = mrow.createCell(k);

                    if (cell != null) {
                        if (cell.getSheet().getWorkbook() == mcell.getSheet()
                                .getWorkbook()) {
                            mcell.setCellStyle(cell.getCellStyle());
                        } else {
                            int stHashCode = cell.getCellStyle().hashCode();
                            XSSFCellStyle newCellStyle = styleMap.get(stHashCode);
                            if (newCellStyle == null) {
                                newCellStyle = mcell.getSheet().getWorkbook()
                                        .createCellStyle();
                                newCellStyle.cloneStyleFrom(cell.getCellStyle());
                                styleMap.put(stHashCode, newCellStyle);
                            }
                            mcell.setCellStyle(newCellStyle);
                        }
                        switch (cell.getCellType()) {
                            case FORMULA:
                                mcell.setCellFormula(cell.getCellFormula());
                                break;
                            case NUMERIC:
                                mcell.setCellValue(cell.getNumericCellValue());
                                break;
                            case BLANK:
                                mcell.setCellType(CellType.BLANK);
                                break;
                            case BOOLEAN:
                                mcell.setCellValue(cell.getBooleanCellValue());
                                break;
                            case ERROR:
                                mcell.setCellErrorValue(cell.getErrorCellValue());
                                break;
                            default:
                                mcell.setCellValue(cell.getStringCellValue());
                                break;
                        }
                    }
                }
            }
        }
    }

    public void removeRow(XSSFWorkbook workbook, Users users) {
        XSSFSheet sheet = workbook.getSheetAt(0);
        List<Integer> removerowNumList = new LinkedList<>();
        for (Row row : sheet) {
            removerowNumList.add(row.getRowNum());
        }
        for (int rowNum : removerowNumList) {
            sheet.removeRow(sheet.getRow(rowNum));
        }
    }


    public List<Integer> sortList(List<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (list.get(i) > list.get(j)) {
                    list.add(i, list.get(j));
                }
            }
        }
        System.out.println(list);
        return list;
    }

    //    @Scheduled(cron = "0 0 1-23 * * *")
//    @Scheduled(cron = "*/100 * * * * *")
    @Scheduled(cron = "0 0 * * * *")
    public void sendUsersFileToGroup() {
        List<Users> usersLinkedList = new LinkedList<>();
        for (Users users : ComponentContainer.usersList) {
            if (users.getSendFileToGroupTime() != null &&
                    users.getSendFileToGroupTime().getHour() == LocalTime.now().getHour()) {
                if (users.isApproveSendFileToGroups()) {
                    usersLinkedList.add(users);
                }
            }
        }
        List<String> employeeAttachedCBList = new LinkedList<>();
        for (Users users : usersLinkedList) {
            if (users.getCbAndEmployees() != null) {
                for (CBAndEmployee cbAndEmployee : users.getCbAndEmployees()) {
                    for (String s : cbAndEmployee.getCBList()) {
                        if (users.getActiveCBList().contains(s)) {
                            filterByCB(s, users, cbAndEmployee.getEmployee());
                            employeeAttachedCBList.add(s);
                        }
                    }
                }
            }
        }

        for (Users users : usersLinkedList) {
            if (users.getCbAndEmployees() != null) {
                for (String s : users.getActiveCBList()) {
                    if (!employeeAttachedCBList.contains(s)) {
                        filterByCB(s, users, "Xodim biriktirilmagan");
                    }
                }
            }
        }

    }

}