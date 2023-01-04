package com.company.controller;

import com.company.container.ComponentContainer;
import com.company.db.Database;
import com.company.entity.Question;
import com.company.entity.Subject;
import com.company.entity.User;
import com.company.enums.AdminStatus;
import com.company.enums.Status;
import com.company.files.WorkWithFiles;
import com.company.files.WorkWithJson;
import com.company.service.QuestionService;
import com.company.service.SubjectService;
import com.company.service.UserService;
import com.company.utils.GenerateId;
import com.company.utils.InlineKeyboardUtils;
import com.company.utils.ReplyKeyboardConst;
import com.company.utils.ReplyKeyboardUtils;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.util.List;
import java.util.UUID;


public class AdminController {
    public static void handleMessage(Message message) {
        try {
            String chatId = String.valueOf(message.getChatId());
            if (ComponentContainer.ADMIN_STATUS.containsKey(chatId)) {
                AdminStatus adminStatus = ComponentContainer.ADMIN_STATUS.get(chatId);

                if (adminStatus.equals(AdminStatus.SHARE_ADVERT)) {
                    for (User user : Database.getUsers()) {
                        if (!user.getChatId().equals(chatId)) {
                            ForwardMessage forwardMessage = new ForwardMessage(user.getChatId(), chatId, message.getMessageId());
                            forwardMessage.setProtectContent(true);
                            ComponentContainer.MY_BOT.sendMsg(forwardMessage);
                        }
                    }

                    SendMessage sendMessage = new SendMessage(chatId, "Advert shared");
                    if (UserService.getUserByChatId(chatId).getStatus().equals(Status.MAIN_ADMIN)) {
                        sendMessage.setReplyMarkup(ReplyKeyboardUtils.getMainAdminMenu());
                    } else {
                        sendMessage.setReplyMarkup(ReplyKeyboardUtils.getAdminMenu());
                    }
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);

                    ComponentContainer.ADMIN_STATUS.remove(chatId);
                    return;
                }
            }
            if (message.hasText()) {
                handleText(message);
            } else if (message.hasPhoto()) {
                handlePhoto(message);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private static void handleText(Message message) {
        String chatId = String.valueOf(message.getChatId());
        String text = message.getText();
        Chat chat = message.getChat();
        SendMessage sendMessage = new SendMessage();

        if (UserService.getUserByChatId(chatId).getStatus().equals(Status.MAIN_ADMIN)
                || UserService.getUserByChatId(chatId).getStatus().equals(Status.ADMIN)) {
            if (text.equals("/start")) {
                sendMessage.setText("Welcome, " + chat.getFirstName() + "\nChoose operation: ");
                if (UserService.getUserByChatId(chatId).getStatus().equals(Status.MAIN_ADMIN)) {
                    sendMessage.setChatId(chatId);
                    sendMessage.setReplyMarkup(ReplyKeyboardUtils.getMainAdminMenu());
                } else {
                    sendMessage.setChatId(chatId);
                    sendMessage.setReplyMarkup(ReplyKeyboardUtils.getAdminMenu());
                }
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

            } else if (UserService.getUserByChatId(chatId).getStatus().equals(Status.MAIN_ADMIN)
                    && text.equals(ReplyKeyboardConst.ADD_ADMIN)) {
                sendMessage.setChatId(chatId);
                if (Database.getUsers().size() - Database.getAdmins().size() == 0) {
                    sendMessage.setText("No users to promote admin");
                    ComponentContainer.ADMIN_STATUS.remove(chatId);
                } else {
                    sendMessage.setText("Choose user to promote main admin or admin:");
                    sendMessage.setReplyMarkup(InlineKeyboardUtils.getUsers());
                    ComponentContainer.ADMIN_STATUS.put(chatId, AdminStatus.ADD_ADMIN);

                }
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

            } else if (ComponentContainer.ADMIN_STATUS.get(chatId) != null
                    && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.ADD_ADMIN)
                    && ComponentContainer.ADMIN_OBJECT.get(chatId) != null) {
                User user = (User) ComponentContainer.ADMIN_OBJECT.get(chatId);
                sendMessage.setChatId(chatId);
                if (text.trim().equalsIgnoreCase("main")) {
                    UserService.changeUserStatus(user, Status.MAIN_ADMIN);
                } else {
                    UserService.changeUserStatus(user, Status.ADMIN);
                }
                sendMessage.setText("Promoted successfully");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

                SendMessage sendMessage1 = new SendMessage(user.getChatId(), "You promoted to admin");
                ComponentContainer.MY_BOT.sendMsg(sendMessage1);

                ComponentContainer.ADMIN_STATUS.remove(chatId);
                ComponentContainer.ADMIN_OBJECT.remove(chatId);
            } else if (UserService.getUserByChatId(chatId).getStatus().equals(Status.MAIN_ADMIN)
                    && text.equals(ReplyKeyboardConst.DELETE_ADMIN)) {
                sendMessage.setChatId(chatId);
                if (Database.getAdmins().size() == 1) {
                    sendMessage.setText("No admins to delete admin");
                    ComponentContainer.ADMIN_STATUS.remove(chatId);
                } else {
                    sendMessage.setText("Choose admin to delete:");
                    sendMessage.setReplyMarkup(InlineKeyboardUtils.getUsers());
                    ComponentContainer.ADMIN_STATUS.put(chatId, AdminStatus.DELETE_ADMIN);
                }
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
            // Admin  menu
            else if (text.equals(ReplyKeyboardConst.SUBJECT_CRUD)) {
                sendMessage.setChatId(chatId);
                sendMessage.setText("Choose operation: ");
                sendMessage.setReplyMarkup(ReplyKeyboardUtils.getSubjectMenu());

                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else if (text.equals(ReplyKeyboardConst.QUESTION_CRUD)) {
                sendMessage.setChatId(chatId);
                sendMessage.setText("Choose operation: ");
                sendMessage.setReplyMarkup(ReplyKeyboardUtils.getQuestionMenu());

                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else if (text.equals(ReplyKeyboardConst.BACK)) {

                ComponentContainer.ADMIN_STATUS.remove(chatId);

                sendMessage.setChatId(chatId);
                sendMessage.setText("Choose operation: ");
                if (UserService.getUserByChatId(chatId).getStatus().equals(Status.MAIN_ADMIN)) {
                    sendMessage.setReplyMarkup(ReplyKeyboardUtils.getMainAdminMenu());
                } else {
                    sendMessage.setReplyMarkup(ReplyKeyboardUtils.getAdminMenu());
                }
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

            }
//            else if (text.equals(ReplyKeyboardConst.SHOW_ALL_TESTS_HISTORY)) {
//
//
//            }
            else if (text.equals(ReplyKeyboardConst.SHARE_ADVERT)) {
                sendMessage.setText("Send what you want to share");
                sendMessage.setChatId(chatId);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                ComponentContainer.ADMIN_STATUS.put(chatId, AdminStatus.SHARE_ADVERT);
            }

            //Subject menu
            else if (text.equals(ReplyKeyboardConst.ADD_SUBJECT)) {
                ComponentContainer.ADMIN_STATUS.put(chatId, AdminStatus.ADD_SUBJECT_NAME);
                sendMessage.setChatId(chatId);
                sendMessage.setText("Enter subject title: ");

                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else if (ComponentContainer.ADMIN_STATUS.get(chatId) != null
                    && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.ADD_SUBJECT_NAME)) {
                String check = SubjectService.check(text);
                sendMessage.setChatId(chatId);
                if (check.equals("ok")) {
                    List<Subject> subjects = Database.getSubjects();
                    subjects.add(new Subject(String.valueOf(UUID.randomUUID()), text));
                    WorkWithJson.subjectsToJson(subjects);
                    sendMessage.setText("Subject created successfully ");
                } else {
                    sendMessage.setText(check);
                }
                ComponentContainer.ADMIN_STATUS.remove(chatId);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

            } else if (text.equals(ReplyKeyboardConst.EDIT_SUBJECT)) {
                List<Subject> subjectList = Database.getSubjects();
                sendMessage.setChatId(chatId);
                if (subjectList.size() > 0) {
                    ComponentContainer.ADMIN_STATUS.put(chatId, AdminStatus.EDIT_SUBJECT_NAME);
                    sendMessage.setText("Choose subject: ");
                    sendMessage.setReplyMarkup(InlineKeyboardUtils.getSubjects());
                } else {
                    sendMessage.setText("No subjects yet");
                }
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

            } else if (ComponentContainer.ADMIN_STATUS.get(chatId) != null
                    && ComponentContainer.PARAM_STATUS.get(chatId) != null
                    && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.EDIT_SUBJECT_NAME)) {

                String s = SubjectService.editSubject(text, (String) ComponentContainer.PARAM_STATUS.get(chatId));
                sendMessage.setText(s);
                sendMessage.setChatId(chatId);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

                ComponentContainer.ADMIN_STATUS.remove(chatId);
                ComponentContainer.PARAM_STATUS.remove(chatId);

            } else if (text.equals(ReplyKeyboardConst.SHOW_SUBJECTS)) {
                List<Subject> subjectList = Database.getSubjects();
                sendMessage.setChatId(chatId);
                if (subjectList.size() == 0) {
                    sendMessage.setText("No subjects yet");
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                } else {
                    File file = WorkWithFiles.getSubjectListFile();
                    InputFile inputFile = new InputFile(file);

                    SendDocument sendDocument = new SendDocument(chatId, inputFile);

                    ComponentContainer.MY_BOT.sendMsg(sendDocument);
                    try {
                        file.delete();
                        file.deleteOnExit();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                ComponentContainer.ADMIN_STATUS.remove(chatId);
            } else if (text.equals(ReplyKeyboardConst.DELETE_SUBJECT)) {

                List<Subject> subjectList = Database.getSubjects();
                sendMessage.setChatId(chatId);
                if (subjectList.size() == 0) {
                    sendMessage.setText("No subjects yet");
                } else {
                    sendMessage.setText("Choose subject to delete: ");
                    sendMessage.setReplyMarkup(InlineKeyboardUtils.getSubjects());
                    sendMessage.setChatId(chatId);
                    ComponentContainer.ADMIN_STATUS.put(chatId, AdminStatus.DELETE_SUBJECT);
                }
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

            }

            ///***************** Question menu
            else if (text.equals(ReplyKeyboardConst.ADD_QUESTION)) {
                List<Subject> subjectList = Database.getSubjects();
                sendMessage.setChatId(chatId);
                if (subjectList.size() == 0) {
                    sendMessage.setText("There is no subjects yet to add question");
                } else {
                    sendMessage.setText("Choose subject to add question: ");
                    sendMessage.setReplyMarkup(InlineKeyboardUtils.getSubjects());
                    ComponentContainer.ADMIN_STATUS.put(chatId, AdminStatus.ADD_QUESTION_BY_SUBJECT_ID);
                }
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else if (text.equals(ReplyKeyboardConst.SHOW_QUESTIONS)) {
                sendMessage.setChatId(chatId);
                if (Database.getSubjects().size() == 0) {
                    sendMessage.setText("No subjects yet");
                } else {
                    sendMessage.setText("Choose subject to see question: ");
                    sendMessage.setReplyMarkup(InlineKeyboardUtils.getSubjects());
                    ComponentContainer.ADMIN_STATUS.put(chatId, AdminStatus.GET_FILE_BY_SUBJECT_ID);
                }
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

            } else if (ComponentContainer.ADMIN_STATUS.get(chatId) != null && ComponentContainer.ADMIN_OBJECT.get(chatId) != null
                    && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.ADD_QUESTION_TITLE)) {
                Question question = (Question) ComponentContainer.ADMIN_OBJECT.get(chatId);
                question.setText(text);
                sendMessage.setText("Enter correct answer: ");
                sendMessage.setChatId(chatId);

                ComponentContainer.MY_BOT.sendMsg(sendMessage);

                ComponentContainer.ADMIN_STATUS.put(chatId, AdminStatus.ADD_QUESTION_CORRECT_ANSWER);

            } else if (ComponentContainer.ADMIN_STATUS.get(chatId) != null
                    && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.ADD_QUESTION_CORRECT_ANSWER)) {
                Question question = (Question) ComponentContainer.ADMIN_OBJECT.get(chatId);
                question.setCorrectAnswer(text);
                sendMessage.setText("Enter wrong answer(e.g 3&4&6 ): ");
                sendMessage.setChatId(chatId);

                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                ComponentContainer.ADMIN_STATUS.put(chatId, AdminStatus.ADD_QUESTION_WRONG_ANSWERS);

            } else if (ComponentContainer.ADMIN_STATUS.get(chatId) != null
                    && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.ADD_QUESTION_WRONG_ANSWERS)) {
                Question question = (Question) ComponentContainer.ADMIN_OBJECT.get(chatId);
                String check = QuestionService.checkQuestionWrongAnswers(text + "&" + question.getCorrectAnswer());
                sendMessage.setChatId(chatId);
                if (check.equals("ok")) {
                    question.setWrongAnswers(text);
                    QuestionService.createQuestion(question);
                    sendMessage.setText("Question added successfully.");
                    ComponentContainer.ADMIN_STATUS.remove(chatId);
                    ComponentContainer.ADMIN_OBJECT.remove(chatId);
                } else {
                    sendMessage.setText(check);
                }
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
            //Delete question
            else if (text.equals(ReplyKeyboardConst.DELETE_QUESTION)) {
                sendMessage.setChatId(chatId);
                List<Question> questionList = Database.getQuestions();
                if (questionList.size() == 0) {
                    sendMessage.setText("There is no question for delete");
                } else {
                    sendMessage.setText("Enter question id for delete.\nTo see quesion's id press show questions: ");
                    ComponentContainer.ADMIN_STATUS.put(chatId, AdminStatus.DELETE_QUESTION);
                }

                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else if (ComponentContainer.ADMIN_STATUS.get(chatId) != null
                    && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.DELETE_QUESTION)) {
                String check = QuestionService.deleteQuestionById(text);
                sendMessage.setText(check);
                sendMessage.setChatId(chatId);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                ComponentContainer.ADMIN_STATUS.remove(chatId);
                ComponentContainer.ADMIN_OBJECT.remove(chatId);
                ComponentContainer.PARAM_STATUS.remove(chatId);
            } else {
                sendMessage.setChatId(chatId);
                sendMessage.setText("Wrong command");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        }

    }

    private static void handlePhoto(Message message) {

    }

    public static void handleCallBack(Message message, String data) {
        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        //Subject menu
        if (ComponentContainer.ADMIN_STATUS.get(chatId) != null
                && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.GET_FILE_BY_SUBJECT_ID)) {
            List<Question> questions = QuestionService.getQuestionsBySubjectId(data);

            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);

            if (questions.size() == 0) {
                sendMessage.setText("No questions by this subject");
                sendMessage.setChatId(chatId);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else {
                File file = WorkWithFiles.getQuestionListFile(data);
                InputFile inputFile = new InputFile(file);

                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(chatId);
                sendDocument.setDocument(inputFile);

                ComponentContainer.MY_BOT.sendMsg(sendDocument);
                try {
                    file.delete();
                    file.deleteOnExit();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            ComponentContainer.ADMIN_STATUS.remove(chatId);
        } else if (ComponentContainer.ADMIN_STATUS.get(chatId) != null
                && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.EDIT_SUBJECT_NAME)) {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);

            sendMessage.setChatId(chatId);
            sendMessage.setText("Enter new subject name: ");
            ComponentContainer.PARAM_STATUS.put(chatId, data);
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (ComponentContainer.ADMIN_STATUS.get(chatId) != null
                && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.DELETE_SUBJECT)) {
            List<Question> questionList = QuestionService.getQuestionsBySubjectId(data);

            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);

            sendMessage.setChatId(chatId);
            if (questionList.size() > 0) {
                sendMessage.setText("There is questions by this subject.\nSo you can not delete this subject");
            } else {
                List<Subject> subjects = Database.getSubjects();
                for (int i = 0; i < subjects.size(); i++) {
                    if (subjects.get(i).getId().equals(data)) {
                        subjects.remove(i);
                        break;
                    }
                }
                WorkWithJson.subjectsToJson(subjects);
                sendMessage.setText("Subject deleted successfully ");
            }
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            ComponentContainer.ADMIN_STATUS.remove(chatId);

        }
        //Question menu
        else if (ComponentContainer.ADMIN_STATUS.get(chatId) != null
                && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.ADD_QUESTION_BY_SUBJECT_ID)) {

            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);

            sendMessage.setChatId(chatId);
            sendMessage.setText("Enter question text(e.g 2*2=?): ");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

            ComponentContainer.ADMIN_STATUS.put(chatId, AdminStatus.ADD_QUESTION_TITLE);
            ComponentContainer.ADMIN_OBJECT.put(chatId, new Question(null,
                    data, null, null, null));

        } else if (ComponentContainer.ADMIN_STATUS.get(chatId) != null
                && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.ADD_ADMIN)) {

            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);

            sendMessage.setChatId(chatId);
            User user = UserService.getUserByChatId(data);
            if (user != null) {
                ComponentContainer.ADMIN_OBJECT.put(chatId, user);
                sendMessage.setText("Send 'main' to promote main admin or send 'admin'");
            } else {
                sendMessage.setText("Error occured");
                ComponentContainer.ADMIN_STATUS.remove(chatId);
                ComponentContainer.ADMIN_OBJECT.remove(chatId);
            }
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (ComponentContainer.ADMIN_STATUS.get(chatId) != null
                && ComponentContainer.ADMIN_STATUS.get(chatId).equals(AdminStatus.DELETE_ADMIN)) {

            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);

            sendMessage.setChatId(chatId);
            User user = UserService.getUserByChatId(data);
            if (user != null) {
                List<String> admins = Database.getAdmins();
                admins.remove(user.getChatId());
                UserService.changeUserStatus(user, Status.USER);
                sendMessage.setText("Removed successfully");

                SendMessage sendMessage1 = new SendMessage(user.getChatId(), "You are taken from admin");
                ComponentContainer.MY_BOT.sendMsg(sendMessage1);

            } else {
                sendMessage.setText("Error occured");
            }
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            ComponentContainer.ADMIN_STATUS.remove(chatId);
            ComponentContainer.ADMIN_OBJECT.remove(chatId);
        }

    }

}
