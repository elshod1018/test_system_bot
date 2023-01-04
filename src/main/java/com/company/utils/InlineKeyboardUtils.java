package com.company.utils;

import com.company.db.Database;
import com.company.entity.Subject;
import com.company.entity.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardUtils {
    public static InlineKeyboardMarkup getSubjects() {

        List<Subject> subjectList = Database.getSubjects();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        if (subjectList.size() < 6) {
            for (Subject subject : subjectList) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(subject.getName());
                button.setCallbackData(subject.getId());
                row.add(button);
                rowList.add(row);
                row = new ArrayList<>();
            }
        } else {
            for (int i = 0; i < subjectList.size(); i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(subjectList.get(i).getName());
                button.setCallbackData(subjectList.get(i).getId());
                row.add(button);
                if (i % 2 == 1) {
                    rowList.add(row);
                    row = new ArrayList<>();
                }
            }
        }

        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup getVariants(List<String> variants) {

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton("A");
        button1.setCallbackData(variants.get(0));

        InlineKeyboardButton button2 = new InlineKeyboardButton("B");
        button2.setCallbackData(variants.get(1));

        InlineKeyboardButton button3 = new InlineKeyboardButton("C");
        button3.setCallbackData(variants.get(2));

        InlineKeyboardButton button4 = new InlineKeyboardButton("D");
        button4.setCallbackData(variants.get(3));
        List<InlineKeyboardButton> row = new ArrayList<>(List.of(button1, button2, button3, button4));
        rowList.add(row);

        return new InlineKeyboardMarkup(rowList);
    }
/*
        public static InlineKeyboardMarkup yesOrNo() {

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton("Yes");
        button1.setCallbackData("_yes_");
        InlineKeyboardButton button2 = new InlineKeyboardButton("No");
        button1.setCallbackData("_no_");

        List<InlineKeyboardButton> row = new ArrayList<>(List.of(button1, button2));
        rowList.add(row);

        return new InlineKeyboardMarkup(rowList);
    }
*/

    public static InlineKeyboardMarkup getUsers() {

        List<User> userList = Database.getUsers();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        if (userList.size() < 6) {
            for (User user : userList) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(user.getFirstName());
                button.setCallbackData(user.getChatId());
                row.add(button);
                rowList.add(row);
                row = new ArrayList<>();
            }
        } else {
            for (int i = 0; i < userList.size(); i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(userList.get(i).getFirstName());
                button.setCallbackData(userList.get(i).getChatId());
                row.add(button);
                if (i % 2 == 1) {
                    rowList.add(row);
                    row = new ArrayList<>();
                }
            }
        }

        return new InlineKeyboardMarkup(rowList);
    }

}
