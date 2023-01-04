package com.company.service;

import com.company.db.Database;
import com.company.entity.Question;
import com.company.files.WorkWithJson;
import com.company.utils.GenerateId;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionService {
    public static Question getQuestionById(String id) {
        List<Question> questions = Database.getQuestions();
        return questions.stream()
                .filter(question -> question.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static List<Question> getQuestionsBySubjectId(String subjectId) {
        List<Question> questions = Database.getQuestions();
        return questions.stream()
                .filter(question -> question.getSubjectId().equals(subjectId))
                .collect(Collectors.toList());

    }

    public static String checkQuestionWrongAnswers(String text) {
        String[] split = text.split("&");
        for (int i = 0; i < split.length-1; i++) {
            for (int j = 0; j < split.length-1; j++) {
                if (i != j && split[i].trim().equalsIgnoreCase(split[j].trim())) {
                    return "There can't be same wrong answers";
                }
            }
        }
        if (split.length < 4) {
            return "Wrong answers must be at least 3";
        }
        if (split.length > 4) {
            return "Error in the answers";
        }
        Set<String> wrongAnswers = new HashSet<>(List.of(split[0], split[1], split[2]));
        String s1 = wrongAnswers.stream().filter(s -> s.trim().equals(split[3].trim()))
                .findFirst()
                .orElse(null);
        if (s1 != null) {
            return "Correct answer can't be in wrong answers";
        }
        if (wrongAnswers.size() != 3) {
            return "There can't be same wrong answers";
        }
        return "ok";
    }

    public static void createQuestion(Question question) {
        question.setId(GenerateId.generateQuestionId());
        List<Question> questionList = Database.getQuestions();
        questionList.add(question);
        WorkWithJson.questionsToJson(questionList);
    }

    public static String deleteQuestionById(String id) {
        List<Question> questionList =Database.getQuestions();
        Question question1 = questionList.stream()
                .filter(question -> question.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (question1 == null) {
            return "Can't find question by this id";
        }
        questionList.remove(question1);
        WorkWithJson.questionsToJson(questionList);
        return "Question deleted successfully";
    }
}
