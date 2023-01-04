package com.company.utils;

import com.company.db.Database;
import com.company.entity.Question;

import java.util.List;

public interface GenerateId {
    static String generateQuestionId(){
        List<Question> questionList = Database.getQuestions();
        if (questionList.size()==0){
            return String.valueOf(1);
        }
        return String.valueOf(Integer.parseInt(questionList.get(questionList.size()-1).getId())+1);
    }
}
