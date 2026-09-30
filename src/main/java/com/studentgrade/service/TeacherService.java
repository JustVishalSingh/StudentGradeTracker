package com.studentgrade.service;

import com.studentgrade.dao.TeacherDAO;
import com.studentgrade.exception.ValidationException;
import com.studentgrade.model.Teacher;
import com.studentgrade.util.ValidationUtil;
import java.util.List;

public class TeacherService {
    private final TeacherDAO teacherDAO = new TeacherDAO();

    public void addTeacher(Teacher teacher) {
        validate(teacher);
        teacherDAO.add(teacher);
    }

    public List<Teacher> getAllTeachers() {
        return teacherDAO.getAll();
    }

    public void updateTeacher(Teacher teacher) {
        validate(teacher);
        teacherDAO.update(teacher);
    }

    public void deleteTeacher(int id) {
        teacherDAO.delete(id);
    }

    public Teacher findByUserId(int userId) {
        return teacherDAO.findByUserId(userId);
    }

    private void validate(Teacher t) {
        if (!ValidationUtil.isNotEmpty(t.getName()))
            throw new ValidationException("Teacher name is required.");
        if (!ValidationUtil.isNotEmpty(t.getEmail()))
            throw new ValidationException("Email is required.");
        if (!ValidationUtil.isValidEmail(t.getEmail()))
            throw new ValidationException("Invalid email format.");
        if (!ValidationUtil.isNotEmpty(t.getDepartment()))
            throw new ValidationException("Department is required.");
    }
}
