package com.company.files;

import com.company.db.Database;
import com.company.entity.Question;
import com.company.entity.Subject;
import com.company.entity.TestHistory;
import com.company.service.QuestionService;
import com.company.service.SubjectService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;;
import java.io.IOException;
import java.util.List;

public class WorkWithFiles {
    static final String MAIN_FILE = "src/main/resources";

    public static File getSubjectListFile() {
        File file = new File(MAIN_FILE, "subjects.pdf");

        try (PdfWriter pdfWriter = new PdfWriter(file);
             PdfDocument pdfDocument = new PdfDocument(pdfWriter);
             Document document = new Document(pdfDocument)) {

            Paragraph paragraph = new Paragraph("Subjects");
            paragraph.setTextAlignment(TextAlignment.CENTER);
            paragraph.setBold();
            document.add(paragraph);

            Table table = new Table(2);
            table.addCell("No");
            table.addCell("Name");

            int i = 1;
            for (Subject subject : Database.getSubjects()) {
                table.addCell(String.valueOf(i));
                table.addCell(subject.getName());
                i++;
            }
            document.add(table);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File getTestHistoryFile(List<TestHistory>testHistoryList) {
        File file = new File(MAIN_FILE, "testHistories.pdf");

        try (PdfWriter pdfWriter = new PdfWriter(file);
             PdfDocument pdfDocument = new PdfDocument(pdfWriter);
             Document document = new Document(pdfDocument)) {

            Paragraph paragraph = new Paragraph("User test history");
            paragraph.setTextAlignment(TextAlignment.CENTER);
            paragraph.setBold();
            document.add(paragraph);

            Table table = new Table(5);
            table.setAutoLayout();
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);

            table.addCell("Subject");
            table.addCell("Question count");
            table.addCell("Your score");
            table.addCell("Start time");
            table.addCell("Finish time");

            for (TestHistory testHistory: testHistoryList) {
                table.addCell(SubjectService.getSubjectById(testHistory.getSubjectId()).getName());
                table.addCell(String.valueOf(testHistory.getCount()));
                table.addCell(String.valueOf(testHistory.getScore()));
                table.addCell(testHistory.getStartedAt());
                table.addCell(testHistory.getFinishedAt());
            }
            document.add(table);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File getQuestionListFile(String subjectId) {
        File file = new File(MAIN_FILE, "questions.pdf");
//        List<Subject> subjectList = Database.SUBJECT_LIST;
        try (PdfWriter pdfWriter = new PdfWriter(file);
             PdfDocument pdfDocument = new PdfDocument(pdfWriter);
             Document document = new Document(pdfDocument)) {

            Subject subject = SubjectService.getSubjectById(subjectId);
            Paragraph paragraph = new Paragraph(subject.getName());
            paragraph.setTextAlignment(TextAlignment.CENTER);
            paragraph.setBold();
            document.add(paragraph);

            Table table = new Table(5);
            table.addCell("No");
            table.addCell("Question title");
            table.addCell("Correct answer");
            table.addCell("Wrong answers");
            table.addCell("Question id");

            int j = 1;
            List<Question>questions=QuestionService.getQuestionsBySubjectId(subjectId);
            for (Question question :questions ) {
                    table.addCell(String.valueOf(j));
                    table.addCell(question.getText());
                    table.addCell(question.getCorrectAnswer());
                    table.addCell(String.valueOf(List.of(question.getWrongAnswers().split("&"))));
                    table.addCell(question.getId());
                    j++;
            }
            document.add(table);

        } catch (IOException e) {
           e.printStackTrace();
        }
        return file;
    }
}
