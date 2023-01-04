package com.company.controller;

import com.company.container.ComponentContainer;
import com.company.db.Database;
import com.company.entity.Question;
import com.company.entity.Subject;
import com.company.entity.TestHistory;
import com.company.enums.UserStatus;
import com.company.files.WorkWithFiles;
import com.company.files.WorkWithJson;
import com.company.service.QuestionService;
import com.company.service.TestHistoryService;
import com.company.utils.InlineKeyboardUtils;
import com.company.utils.ReplyKeyboardConst;
import com.company.utils.ReplyKeyboardUtils;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {
    public static void handleMessage(Message message) {
        String chatId = String.valueOf(message.getChatId());
        List<String> admins = Database.getAdmins();
        System.out.println(admins);
        System.out.println(Database.getUsers());
        if (admins.contains(chatId)) {
            AdminController.handleMessage(message);
            return;
        }
        SendMessage sendMessage = new SendMessage();
        String text = message.getText();
        if (text.equals("/start")) {
            sendMessage.setText("Welcome, " + message.getChat().getFirstName());
            sendMessage.setReplyMarkup(ReplyKeyboardUtils.getUserMenu());
            sendMessage.setChatId(chatId);
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(ReplyKeyboardConst.SOLVE_QUIZ)) {
            sendMessage.setChatId(chatId);
            List<Subject> subjectList = Database.getSubjects();
            if (subjectList.size() == 0) {
                sendMessage.setText("No subjects yet.");
            } else {
                sendMessage.setText("Choose subject for start quizz: ");
                sendMessage.setReplyMarkup(InlineKeyboardUtils.getSubjects());
                ComponentContainer.USER_STATUS.put(chatId, UserStatus.SOLVE_QUIZ);
            }
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(ReplyKeyboardConst.TEST_HISTORY)) {
            sendMessage.setChatId(chatId);
            List<TestHistory> testHistoryList = TestHistoryService.getUserHistoryByChatId(chatId);
            if (testHistoryList.size() == 0) {
                sendMessage.setText("You haven't solved test yet");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else {
                File file = WorkWithFiles.getTestHistoryFile(testHistoryList);
                InputFile inputFile = new InputFile(file);
                SendDocument sendDocument = new SendDocument(chatId, inputFile);
                ComponentContainer.MY_BOT.sendMsg(sendDocument);

                if (file.exists()) {
                    file.delete();
                }
            }
        } else if (ComponentContainer.USER_STATUS.get(chatId) != null &&
                ComponentContainer.USER_STATUS.get(chatId).equals(UserStatus.CONTACT_WITH_ADMIN)) {
            List<String> adminList = Database.getAdmins();
            ForwardMessage forwardMessage = new ForwardMessage();
            forwardMessage.setMessageId(message.getMessageId());
            forwardMessage.setFromChatId(chatId);
            for (int i = 0; i < adminList.size(); i++) {
                forwardMessage.setChatId(adminList.get(i));

                ComponentContainer.MY_BOT.sendMsg(forwardMessage);
            }
            SendMessage sendMessage1 = new SendMessage();
            sendMessage1.setChatId(chatId);
            sendMessage1.setText("Soon, admins contact with you.Message sent to admins");
            ComponentContainer.MY_BOT.sendMsg(sendMessage1);
            ComponentContainer.USER_STATUS.remove(chatId);
        }
    }

    public static void handleCallBack(Message message, String data) {
        String chatId = String.valueOf(message.getChatId());
        List<String> admins = Database.getAdmins();
        System.out.println(admins);
        System.out.println(Database.getUsers());
        if (admins.contains(chatId)) {
            AdminController.handleCallBack(message, data);
            return;
        }
        SendMessage sendMessage = new SendMessage();
        if (ComponentContainer.USER_STATUS.get(chatId) != null &&
                ComponentContainer.USER_STATUS.get(chatId).equals(UserStatus.SOLVE_QUIZ)
                && ComponentContainer.USER_OBJECT.get(chatId) == null) {

            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);

            List<Question> questionList = QuestionService.getQuestionsBySubjectId(data);

            sendMessage.setChatId(chatId);
            if (questionList.size() == 0) {
                sendMessage.setText("No questions by this subject");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else {
                List<Question> userQuestions;
                Collections.shuffle(questionList);
                if (questionList.size() > 10) {
                    userQuestions = questionList.stream()
                            .limit(10).collect(Collectors.toList());
                } else {
                    userQuestions = questionList;
                }
                sendMessage.setText("Go");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

                ComponentContainer.PARAM_STATUS.put(chatId, data);
                ComponentContainer.USERS_TEST_LIST.put(chatId, userQuestions);

                SendMessage sendMessage1 = new SendMessage();
                if (ComponentContainer.USER_OBJECT.get(chatId) == null) {
                    List<Boolean> questionStatus = new ArrayList<>();
                    ComponentContainer.USER_OBJECT.put(chatId, questionStatus);
                    TestHistory testHistory = new TestHistory(String.valueOf(UUID.randomUUID()),
                            (String) ComponentContainer.PARAM_STATUS.get(chatId), chatId, 0, 0,
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), null);
                    ComponentContainer.PARAM_STATUS.put(chatId, testHistory);
                }
                @SuppressWarnings("unchecked")
                List<Boolean> questionStatus = (List<Boolean>) ComponentContainer.USER_OBJECT.get(chatId);
                sendMessage1.setChatId(chatId);

                Question question = questionList.get(questionStatus.size());
                String[] split = question.getWrongAnswers().split("&");
                List<String> variants = new ArrayList<>(List.of(split));
                variants.add(question.getCorrectAnswer());
                Collections.shuffle(variants);

                sendMessage1.setText(question.getText() + "\nA) " + variants.get(0) + "\nB) " + variants.get(1)
                        + "\nC) " + variants.get(2) + "\nD) " + variants.get(3));
                sendMessage1.setReplyMarkup(InlineKeyboardUtils.getVariants(variants));

                ComponentContainer.MY_BOT.sendMsg(sendMessage1);
            }

        } else if (ComponentContainer.USER_STATUS.get(chatId) != null &&
                ComponentContainer.USER_STATUS.get(chatId).equals(UserStatus.SOLVE_QUIZ)
                && ComponentContainer.PARAM_STATUS.get(chatId) != null
                && ComponentContainer.USERS_TEST_LIST.get(chatId) != null
                && ComponentContainer.USER_OBJECT.get(chatId) != null) {

            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);
            @SuppressWarnings("unchecked")
            List<Boolean> questionStatus = (List<Boolean>) ComponentContainer.USER_OBJECT.get(chatId);
            sendMessage.setChatId(chatId);
            List<Question> questionList = ComponentContainer.USERS_TEST_LIST.get(chatId);
            Question question = questionList.get(questionStatus.size());

            if (data.equals(question.getCorrectAnswer())) {
                questionStatus.add(true);
                sendMessage.setText(questionStatus.size() + " ✅");
            } else {
                questionStatus.add(false);
                sendMessage.setText(questionStatus.size() + " ❌");
            }
            ComponentContainer.USER_OBJECT.put(chatId, questionStatus);

            ComponentContainer.MY_BOT.sendMsg(sendMessage);

            if (questionStatus.size() == questionList.size()) {
                List<Boolean> score = questionStatus.stream()
                        .filter(aBoolean -> aBoolean.equals(true)).toList();

                TestHistory testHistory = (TestHistory) ComponentContainer.PARAM_STATUS.get(chatId);

                testHistory.setFinishedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

                testHistory.setCount(questionList.size());
                testHistory.setScore(score.size());
                List<TestHistory> testHistories = Database.getTestHistories();
                testHistories.add(testHistory);
                WorkWithJson.testHistoriesToJson(testHistories);

                sendMessage.setText("Test is ended.Your score " + score.size() + " from " + questionList.size());
                sendMessage.setChatId(chatId);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

                ComponentContainer.USERS_TEST_LIST.remove(chatId);
                ComponentContainer.USER_STATUS.remove(chatId);
                ComponentContainer.PARAM_STATUS.remove(chatId);
                ComponentContainer.USER_OBJECT.remove(chatId);
            } else {
                question = questionList.get(questionStatus.size());
                String[] split = question.getWrongAnswers().split("&");
                List<String> variants = new ArrayList<>(List.of(split));
                variants.add(question.getCorrectAnswer());
                Collections.shuffle(variants);

                sendMessage.setText(question.getText() + "\nA) " + variants.get(0) + "\nB) " + variants.get(1)
                        + "\nC) " + variants.get(2) + "\nD) " + variants.get(3));
                sendMessage.setReplyMarkup(InlineKeyboardUtils.getVariants(variants));

                ComponentContainer.MY_BOT.sendMsg(sendMessage);

            }
        }


    }
}
