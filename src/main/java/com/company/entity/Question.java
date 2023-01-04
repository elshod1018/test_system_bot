package com.company.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    private String id;
    private String subjectId;
    private String text;
    private String correctAnswer;
    private String wrongAnswers;
}
