package com.company.entity;

import lombok.Data;
import lombok.ToString;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@ToString
public class Users {
    private Long chatId;
    private String lastSendText;
    private String newSendText;
    private String replyMurkubText;
    private String sheetName;
    private List<String> columnList;
    private Integer columnCount;
    private Map<String, List<String>> columnsLater;
    private List<String> groupList;
    private String link;
    private ReplyKeyboard newReplyKeyboard;
    private ReplyKeyboard lastReplyKeyboard;
    private List<Group> allGroupList;
    private List<CBAndEmployee> cbAndEmployees;
    private String comment;
    private Integer lastMessageId;
    private LocalTime sendFileToGroupTime;
    private List<String> activeCBList;
    private List<Integer> lastFilterRowNumList;
    private boolean approveSendFileToGroups;

    public void addColumnsLater(String column, List<String> laterList) {
        if (columnsLater != null) {
            Map<String, List<String>> listMap = columnsLater;
            listMap.put(column, laterList);
            columnsLater = listMap;
        } else {
            Map<String, List<String>> listMap = new LinkedHashMap<>();
            listMap.put(column, laterList);
            columnsLater = listMap;
        }
        System.out.println(columnsLater);
    }

    public void addAllGroupList(Group group) {
        if (allGroupList == null) {
            allGroupList = new LinkedList<>();
            allGroupList.add(group);
        } else {
            if (!allGroupList.contains(group)) {
                allGroupList.add(group);
            }
        }
    }

    public void deleteUsersGroupByChatId(Long groupChatId) {
        if (groupChatId != null) {
            allGroupList.removeIf(group -> group.getChatId().equals(groupChatId));
        }
    }

    public void addCBAndEmployee(CBAndEmployee cbAndEmployee) {
        if (cbAndEmployee != null) {
            if (cbAndEmployees == null) {
                cbAndEmployees = new LinkedList<>();
                cbAndEmployees.add(cbAndEmployee);
            } else {
                cbAndEmployees.add(cbAndEmployee);
            }
        }
    }

    public void addActiveCB(String activeCB){
        if (activeCBList != null){
            if (!activeCBList.contains(activeCB)){
                activeCBList.add(activeCB);
            }
        }else {
            activeCBList = new LinkedList<>();
            if (activeCB != null) {
                activeCBList.add(activeCB);
            }
        }
    }

    public void addLastFilterRowNum(int row){
        if (lastFilterRowNumList != null){
            if (!lastFilterRowNumList.contains(row)){
                lastFilterRowNumList.add(row);
            }
        }else {
            lastFilterRowNumList = new LinkedList<>();
            lastFilterRowNumList.add(row);
        }
    }
}
