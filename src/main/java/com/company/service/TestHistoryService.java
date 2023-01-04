package com.company.service;

import com.company.db.Database;
import com.company.entity.TestHistory;

import java.util.List;
import java.util.stream.Collectors;

public class TestHistoryService {
    public static List<TestHistory> getUserHistoryByChatId(String chatId){
        return Database.getTestHistories().stream()
                .filter(testHistory -> testHistory.getChatId().equals(chatId))
                .collect(Collectors.toList());
    }
}
