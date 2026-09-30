package com.studentgrade.service;

import com.studentgrade.dao.SubjectDAO;
import com.studentgrade.exception.ValidationException;
import com.studentgrade.model.Subject;
import com.studentgrade.util.ValidationUtil;
import java.util.List;

public class SubjectService {
    private final SubjectDAO subjectDAO = new SubjectDAO();

    public void addSubject(Subject subject) {
        validate(subject);
        subjectDAO.add(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectDAO.getAll();
    }

    public List<Subject> getSubjectsByTeacher(int teacherId) {
        return subjectDAO.getByTeacherId(teacherId);
    }

    public void updateSubject(Subject subject) {
        validate(subject);
        subjectDAO.update(subject);
    }

    public void deleteSubject(int id) {
        subjectDAO.delete(id);
    }

    private void validate(Subject s) {
        if (!ValidationUtil.isNotEmpty(s.getSubjectCode()))
            throw new ValidationException("Subject code is required.");
        if (!ValidationUtil.isNotEmpty(s.getSubjectName()))
            throw new ValidationException("Subject name is required.");
        if (!ValidationUtil.isValidCredits(s.getCredits()))
            throw new ValidationException("Credits must be between 1 and 6.");
    }
}
