package com.studentgrade.service;

import com.studentgrade.dao.GradeDAO;
import com.studentgrade.dao.StudentDAO;
import com.studentgrade.dao.SubjectDAO;
import com.studentgrade.dao.TeacherDAO;
import com.studentgrade.model.Grade;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyticsService {
    private final StudentDAO studentDAO = new StudentDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final GradeDAO gradeDAO = new GradeDAO();

    public int getTotalStudents() { return studentDAO.count(); }
    public int getTotalTeachers() { return teacherDAO.count(); }
    public int getTotalSubjects() { return subjectDAO.count(); }

    public double getOverallAverage() {
        List<Grade> grades = gradeDAO.getAll();
        if (grades.isEmpty()) return 0.0;
        return grades.stream().mapToDouble(Grade::getMarks).average().orElse(0.0);
    }

    public double getHighestMark() {
        List<Grade> grades = gradeDAO.getAll();
        return grades.stream().mapToDouble(Grade::getMarks).max().orElse(0.0);
    }

    public double getLowestMark() {
        List<Grade> grades = gradeDAO.getAll();
        return grades.stream().mapToDouble(Grade::getMarks).min().orElse(0.0);
    }

    public double getPassPercentage() {
        List<Grade> grades = gradeDAO.getAll();
        if (grades.isEmpty()) return 0.0;
        long passed = grades.stream().filter(g -> g.getMarks() >= 40).count();
        return (passed * 100.0) / grades.size();
    }

    public Map<String, Long> getGradeDistribution() {
        List<Grade> grades = gradeDAO.getAll();
        return grades.stream()
            .collect(Collectors.groupingBy(Grade::getGradeLetter, Collectors.counting()));
    }

    public Map<String, Double> getSubjectAverages() {
        List<Grade> grades = gradeDAO.getAll();
        return grades.stream()
            .collect(Collectors.groupingBy(Grade::getSubjectName,
                     Collectors.averagingDouble(Grade::getMarks)));
    }
}
