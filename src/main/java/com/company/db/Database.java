package com.company.db;

import com.company.entity.*;
import com.company.enums.Status;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Database {

    static public List<User> getUsers() {
        List<User> userList = new ArrayList<>();
        File file = new File("src/main/resources/users.json");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            userList = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }

    static public List<String> getAdmins() {
        List<String> adminList = new ArrayList<>();
        List<User> users = getUsers();
        for (User user : users) {
            if (user.getStatus().equals(Status.ADMIN)
                    || user.getStatus().equals(Status.MAIN_ADMIN)) {
                adminList.add(user.getChatId());
            }
        }
        return adminList;
    }

    static public List<Subject> getSubjects() {
        List<Subject> subjectList = new ArrayList<>();
        File file = new File("src/main/resources/subjects.json");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            Type type = new TypeToken<List<Subject>>() {
            }.getType();
            subjectList = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subjectList;
    }

    static public List<Question> getQuestions() {
        List<Question> questionList = new ArrayList<>();
        File file = new File("src/main/resources/questions.json");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            Type type = new TypeToken<List<Question>>() {
            }.getType();
            questionList = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionList;
    }

    static public List<TestHistory> getTestHistories() {
        List<TestHistory> testHistories = new ArrayList<>();
        File file = new File("src/main/resources/testHistories.json");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            Type type = new TypeToken<List<TestHistory>>() {
            }.getType();
            testHistories = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testHistories;
    }

}
