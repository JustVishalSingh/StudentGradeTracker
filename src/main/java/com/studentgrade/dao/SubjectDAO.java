package com.studentgrade.dao;

import com.studentgrade.database.DatabaseConnection;
import com.studentgrade.exception.DatabaseException;
import com.studentgrade.model.Subject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public void add(Subject subject) {
        String sql = "INSERT INTO subjects (subject_code, subject_name, credits, teacher_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subject.getSubjectCode());
            ps.setString(2, subject.getSubjectName());
            ps.setInt(3, subject.getCredits());
            if (subject.getTeacherId() != null) ps.setInt(4, subject.getTeacherId());
            else ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DatabaseException("A subject with this code already exists.", e);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add subject: " + e.getMessage(), e);
        }
    }

    public List<Subject> getAll() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT * FROM subjects ORDER BY subject_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve subjects.", e);
        }
        return list;
    }

    public List<Subject> getByTeacherId(int teacherId) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT * FROM subjects WHERE teacher_id = ? ORDER BY subject_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve subjects for teacher.", e);
        }
        return list;
    }

    public void update(Subject subject) {
        String sql = "UPDATE subjects SET subject_name=?, credits=?, teacher_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subject.getSubjectName());
            ps.setInt(2, subject.getCredits());
            if (subject.getTeacherId() != null) ps.setInt(3, subject.getTeacherId());
            else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, subject.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update subject.", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM subjects WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete subject.", e);
        }
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM subjects";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count subjects.", e);
        }
        return 0;
    }

    private Subject mapRow(ResultSet rs) throws SQLException {
        return new Subject(
            rs.getInt("id"),
            rs.getString("subject_code"),
            rs.getString("subject_name"),
            rs.getInt("credits"),
            (Integer) rs.getObject("teacher_id")
        );
    }
}
