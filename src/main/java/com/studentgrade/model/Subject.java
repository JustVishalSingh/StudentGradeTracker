package com.studentgrade.model;

public class Subject {
    private int id;
    private String subjectCode;
    private String subjectName;
    private int credits;
    private Integer teacherId;

    public Subject() {}

    public Subject(int id, String subjectCode, String subjectName, int credits, Integer teacherId) {
        this.id = id;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.credits = credits;
        this.teacherId = teacherId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }
}
