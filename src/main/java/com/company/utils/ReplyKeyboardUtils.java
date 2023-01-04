package com.company.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class ReplyKeyboardUtils {
    public static ReplyKeyboard getUserMenu() {
        return getMarkUp(getRowList(
                getRow(
                        getButton(ReplyKeyboardConst.SOLVE_QUIZ),
                        getButton(ReplyKeyboardConst.TEST_HISTORY)
                )
//                getRow(
//                        getButton(ReplyKeyboardConst.CONTACT_WITH_ADMIN)
//                )
        ));
    }

    public static ReplyKeyboard getQuestionMenu() {
        return getMarkUp(getRowList(
                getRow(
                        getButton(ReplyKeyboardConst.ADD_QUESTION)
//                        getButton(ReplyKeyboardConst.EDIT_QUESTION)
                ),
                getRow(
                        getButton(ReplyKeyboardConst.DELETE_QUESTION),
                        getButton(ReplyKeyboardConst.SHOW_QUESTIONS)
                ),
                getRow(
                        getButton(ReplyKeyboardConst.BACK)
                )

        ));
    }

    public static ReplyKeyboard getSubjectMenu() {
        return getMarkUp(getRowList(
                getRow(
                        getButton(ReplyKeyboardConst.ADD_SUBJECT),
                        getButton(ReplyKeyboardConst.EDIT_SUBJECT)
                ),
                getRow(
                        getButton(ReplyKeyboardConst.DELETE_SUBJECT),
                        getButton(ReplyKeyboardConst.SHOW_SUBJECTS)
                ),
                getRow(
                        getButton(ReplyKeyboardConst.BACK)
                )

        ));
    }

    public static ReplyKeyboard getAdminMenu() {
        return getMarkUp(getRowList(
                getRow(
                        getButton(ReplyKeyboardConst.SUBJECT_CRUD),
                        getButton(ReplyKeyboardConst.QUESTION_CRUD)
                ),
                getRow(
//                        getButton(ReplyKeyboardConst.SHOW_ALL_TESTS_HISTORY),
                        getButton(ReplyKeyboardConst.SHARE_ADVERT)
                )
        ));
    }
    public static ReplyKeyboard getMainAdminMenu() {
        return getMarkUp(getRowList(
                getRow(
                        getButton(ReplyKeyboardConst.SUBJECT_CRUD),
                        getButton(ReplyKeyboardConst.QUESTION_CRUD)
                ),
                getRow(
                        getButton(ReplyKeyboardConst.ADD_ADMIN),
                        getButton(ReplyKeyboardConst.DELETE_ADMIN)
                ),
                getRow(
//                        getButton(ReplyKeyboardConst.SHOW_ALL_TESTS_HISTORY),
                        getButton(ReplyKeyboardConst.SHARE_ADVERT)
                )
        ));
    }


    private static ReplyKeyboard getMarkUp(List<KeyboardRow> rowList) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(rowList);
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        return markup;
    }

    private static List<KeyboardRow> getRowList(KeyboardRow... rows) {
        return List.of(rows);
    }

    private static KeyboardRow getRow(KeyboardButton... buttons) {
        return new KeyboardRow(List.of(buttons));
    }

    private static KeyboardButton getButton(String demo) {
        return new KeyboardButton(demo);
    }
}
