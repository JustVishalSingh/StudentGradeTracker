package com.studentgrade.util;

import com.studentgrade.model.Grade;
import com.studentgrade.model.Student;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Utility class for exporting data to CSV files.
 * Demonstrates Java File I/O using FileWriter and PrintWriter.
 */
public class CSVExportUtil {

    /**
     * Exports a list of students to a CSV file.
     * @param students List of Student objects
     * @param filePath Destination file path (e.g., "reports/students.csv")
     */
    public static void exportStudents(List<Student> students, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // CSV Header
            writer.println("Roll Number,Name,Email,Phone,Course,Semester");
            // Data rows
            for (Student s : students) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d%n",
                    s.getRollNumber(), s.getName(), s.getEmail(),
                    s.getPhone() != null ? s.getPhone() : "",
                    s.getCourse(), s.getSemester());
            }
        }
    }

    /**
     * Exports a list of grades to a CSV file.
     * @param grades List of Grade objects (should have display fields populated via JOIN)
     * @param filePath Destination file path
     */
    public static void exportGrades(List<Grade> grades, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // CSV Header
            writer.println("Roll Number,Student Name,Subject Code,Subject Name,Marks,Grade");
            // Data rows
            for (Grade g : grades) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",%.1f,\"%s\"%n",
                    g.getRollNumber(), g.getStudentName(),
                    g.getSubjectCode(), g.getSubjectName(),
                    g.getMarks(), g.getGradeLetter());
            }
        }
    }
}
