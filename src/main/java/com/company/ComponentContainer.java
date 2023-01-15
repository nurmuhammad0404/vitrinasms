package com.company;

import com.company.entity.Users;

import java.util.LinkedList;
import java.util.List;

public abstract class ComponentContainer {
    public static MyTelegramBot MY_TELEGRAM_BOT;
    public static List<Users> usersList;
    public static String excelFileUrl = "src/main/resources/excel/";
    public static String filterFileName = "src/main/resources/excel/filter";
    public static String imagePath = "src/main/resources/image/filter";
    public static String copyFileName;
}
