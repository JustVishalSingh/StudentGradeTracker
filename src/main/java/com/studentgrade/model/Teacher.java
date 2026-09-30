package com.studentgrade.model;

public class Teacher extends Person {
    private String department;
    private Integer userId;

    public Teacher() {
        super();
    }

    public Teacher(int id, String name, String email, String department, Integer userId) {
        super(id, name, email);
        this.department = department;
        this.userId = userId;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}
