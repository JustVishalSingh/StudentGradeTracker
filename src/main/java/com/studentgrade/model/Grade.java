package com.studentgrade.model;

import java.sql.Timestamp;

public class Grade {
    // Core DB fields
    private int id;
    private int studentId;
    private int subjectId;
    private double marks;
    private String gradeLetter;
    private Timestamp createdAt;

    // Display fields — populated by JOIN queries in GradeDAO (not stored in DB)
    private String studentName;
    private String rollNumber;
    private String subjectName;
    private String subjectCode;

    public Grade() {}

    public Grade(int id, int studentId, int subjectId, double marks, String gradeLetter) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.marks = marks;
        this.gradeLetter = gradeLetter;
    }

    // === Core Getters/Setters ===
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public double getMarks() { return marks; }
    public void setMarks(double marks) { this.marks = marks; }

    public String getGradeLetter() { return gradeLetter; }
    public void setGradeLetter(String gradeLetter) { this.gradeLetter = gradeLetter; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // === Display Getters/Setters (from JOINs) ===
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
}
