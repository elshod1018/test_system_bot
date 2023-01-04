package com.company.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestHistory {
    private String id;
    private String subjectId;
    private String chatId;
    private int count;
    private int score;
    private String startedAt;
    private String finishedAt;
    //LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MMM.yyyy HH:mm:ss"));
}
