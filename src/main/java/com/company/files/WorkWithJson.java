package com.company.files;

import com.company.db.Database;
import com.company.entity.Question;
import com.company.entity.Subject;
import com.company.entity.TestHistory;
import com.company.entity.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class WorkWithJson {
    static String MAIN_FILE="src/main/resources";
    public static void usersToJson(List<User> userList) {
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        File file = new File(MAIN_FILE,"users.json");
        try (PrintWriter writer = new PrintWriter(file)) {

            writer.write(gson.toJson(userList));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void subjectsToJson(List<Subject>subjectList) {
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        File file = new File(MAIN_FILE,"subjects.json");
        try (PrintWriter writer = new PrintWriter(file)) {

            writer.write(gson.toJson(subjectList));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void questionsToJson(List<Question>questionList) {
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        File file = new File(MAIN_FILE,"questions.json");
        try (PrintWriter writer = new PrintWriter(file)) {

            writer.write(gson.toJson(questionList));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void testHistoriesToJson(List<TestHistory>testHistoryList) {
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        File file = new File(MAIN_FILE,"testHistories.json");
        try (PrintWriter writer = new PrintWriter(file)) {

            writer.write(gson.toJson(testHistoryList));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
