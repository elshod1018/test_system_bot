package com.company.bot;

import com.company.container.ComponentContainer;
import com.company.controller.MainController;
import com.company.db.Database;
import com.company.entity.User;
import com.company.enums.Status;
import com.company.files.WorkWithJson;
import com.company.service.UserService;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyBot extends TelegramLongPollingBot {
    @Override
    public String getBotToken() {
        return ComponentContainer.BOT_TOKEN;
    }
    @Override
    public String getBotUsername() {
        return ComponentContainer.BOT_USERNAME;
    }
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.hasMessage() ? update.getMessage() : update.getCallbackQuery().getMessage();
        String chatId = String.valueOf(message.getChatId());
        Chat chat = message.getChat();

        List<User> users = Database.getUsers();
        if (Objects.isNull(UserService.getUserByChatId(chatId))) {
            User user = new User(chatId, chat.getFirstName(), chat.getLastName(), chat.getUserName());
            users.add(user);
            WorkWithJson.usersToJson(users);
        }
        if (ComponentContainer.MAIN_ADMINS.contains(chatId)) {
            User user = new User(chatId, chat.getFirstName(), chat.getLastName(), chat.getUserName());
            UserService.changeUserStatus(user, Status.MAIN_ADMIN);
        }
        if (update.hasMessage()) {
            MainController.handleMessage(message);
        }
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            MainController.handleCallBack(message, data);
        }
    }

    public void sendMsg(Object obj) {
        try {
            if (obj instanceof SendMessage sendMessage) {
                execute(sendMessage);
            } else if (obj instanceof EditMessageText editMessageText) {
                execute(editMessageText);
            } else if (obj instanceof DeleteMessage deleteMessage) {
                execute(deleteMessage);
            } else if (obj instanceof SendPhoto sendPhoto) {
                execute(sendPhoto);
            } else if (obj instanceof SendDocument sendDocument) {
                execute(sendDocument);
            } else if (obj instanceof ForwardMessage forwardMessage) {
                execute(forwardMessage);
            } else if (obj instanceof EditMessageReplyMarkup editMessageReplyMarkup) {
                execute(editMessageReplyMarkup);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
