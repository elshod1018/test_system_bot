package com.company.service;

import com.company.db.Database;
import com.company.entity.User;
import com.company.enums.Status;
import com.company.files.WorkWithJson;

import java.util.List;

public class UserService {
    public static User getUserByChatId(String chatId){
        return Database.getUsers().stream()
                .filter(user -> user.getChatId().equals(chatId))
                .findFirst()
                .orElse(null);
    }


    public static void changeUserStatus(User user, Status status) {
        List<User> users = Database.getUsers();
        users.stream()
                .filter(user1 -> user1.getChatId().equals(user.getChatId()))
                .findFirst().ifPresent(user1 -> user1.setStatus(status));
        WorkWithJson.usersToJson(users);
    }

}
