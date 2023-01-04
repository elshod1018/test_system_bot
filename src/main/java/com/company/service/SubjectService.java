package com.company.service;

import com.company.db.Database;
import com.company.entity.Subject;
import com.company.files.WorkWithJson;

import java.util.List;
import java.util.Optional;

public class SubjectService {
    public static Subject getSubjectById(String id) {
        List<Subject> subjects = Database.getSubjects();
        return subjects.stream()
                .filter(subject -> subject.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static String check(String name) {
        List<Subject> subjects = Database.getSubjects();
        if (name == null || name.isBlank()) {
            return "Subject name is required";
        }
        Subject subject1 = subjects.stream()
                .filter(subject -> subject.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
        if (subject1 != null) {
            return "This subject already exist";
        }
        return "ok";
    }

    public static String editSubject(String newSubjectName, String subjectId) {

        try {
            if (newSubjectName == null || newSubjectName.isBlank()) {
                return "Subject name is required";
            }
            List<Subject> subjects = Database.getSubjects();

            Optional<Subject> subjectOptional = subjects.stream()
                    .filter(subject -> subject.getName().equalsIgnoreCase(newSubjectName))
                    .findFirst();

            if (subjectOptional.isPresent() && !subjectOptional.get().getId().equals(subjectId)) {
                return "This subject already exists";
            }
            subjects.stream()
                    .filter(subject1 -> subject1.getId().equals(subjectId))
                    .findFirst().ifPresent(subject -> subject.setName(newSubjectName));
            WorkWithJson.subjectsToJson(subjects);

        } catch (RuntimeException e) {
            return e.getMessage();
        }

        return "Subject edited.";
    }

}
