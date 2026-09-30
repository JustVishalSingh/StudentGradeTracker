package com.studentgrade.model;

import java.sql.Timestamp;

public class Student extends Person {
    private String rollNumber;
    private String phone;
    private String course;
    private int semester;
    private Integer userId; // Foreign key to User account (Composition)
    private Timestamp createdAt;

    public Student() {
        super();
    }

    public Student(int id, String rollNumber, String name, String email, String phone, String course, int semester, Integer userId) {
        super(id, name, email);
        this.rollNumber = rollNumber;
        this.phone = phone;
        this.course = course;
        this.semester = semester;
        this.userId = userId;
    }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
