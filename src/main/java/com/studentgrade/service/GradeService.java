package com.studentgrade.service;

import com.studentgrade.dao.GradeDAO;
import com.studentgrade.exception.ValidationException;
import com.studentgrade.model.Grade;
import com.studentgrade.model.Subject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GradeService {
    private final GradeDAO gradeDAO = new GradeDAO();

    /** Save or update a grade (upsert). */
    public void saveGrade(Grade grade) {
        if (grade.getMarks() < 0 || grade.getMarks() > 100)
            throw new ValidationException("Marks must be between 0 and 100.");
        grade.setGradeLetter(calculateGrade(grade.getMarks()));
        gradeDAO.upsert(grade);
    }

    public List<Grade> getAllGrades() {
        return gradeDAO.getAll();
    }

    public List<Grade> getGradesForStudent(int studentId) {
        return gradeDAO.getByStudentId(studentId);
    }

    public List<Grade> getGradesForSubject(int subjectId) {
        return gradeDAO.getBySubjectId(subjectId);
    }

    // === CALCULATIONS (Phase 11) ===

    public String calculateGrade(double marks) {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B+";
        if (marks >= 60) return "B";
        if (marks >= 50) return "C";
        if (marks >= 40) return "D";
        return "F";
    }

    public double calculateAverage(List<Double> marksList) {
        if (marksList == null || marksList.isEmpty()) return 0.0;
        double sum = 0;
        for (double m : marksList) sum += m;
        return sum / marksList.size();
    }

    public double calculateHighest(List<Double> marksList) {
        if (marksList == null || marksList.isEmpty()) return 0.0;
        double max = marksList.get(0);
        for (double m : marksList) if (m > max) max = m;
        return max;
    }

    public double calculateLowest(List<Double> marksList) {
        if (marksList == null || marksList.isEmpty()) return 0.0;
        double min = marksList.get(0);
        for (double m : marksList) if (m < min) min = m;
        return min;
    }

    public double calculatePercentage(List<Double> marksList) {
        return calculateAverage(marksList); // marks are already out of 100
    }

    public boolean isPassOverall(List<Double> marksList) {
        if (marksList == null || marksList.isEmpty()) return false;
        for (double m : marksList) if (m < 40) return false;
        return true;
    }

    /** SGPA: sum(grade_points * credits) / sum(credits) */
    public double calculateSGPA(List<Grade> grades, List<Subject> subjects) {
        double totalPoints = 0;
        int totalCredits = 0;
        for (Grade g : grades) {
            for (Subject s : subjects) {
                if (g.getSubjectId() == s.getId()) {
                    int credits = s.getCredits();
                    totalPoints += getGradePoints(g.getGradeLetter()) * credits;
                    totalCredits += credits;
                    break;
                }
            }
        }
        return totalCredits > 0 ? Math.round((totalPoints / totalCredits) * 100.0) / 100.0 : 0.0;
    }

    private double getGradePoints(String grade) {
        switch (grade) {
            case "A+": return 10;
            case "A":  return 9;
            case "B+": return 8;
            case "B":  return 7;
            case "C":  return 6;
            case "D":  return 5;
            default:   return 0;
        }
    }

    /** Grade distribution for analytics. */
    public Map<String, Long> getGradeDistribution(List<Grade> grades) {
        return grades.stream()
            .collect(Collectors.groupingBy(Grade::getGradeLetter, Collectors.counting()));
    }
}
