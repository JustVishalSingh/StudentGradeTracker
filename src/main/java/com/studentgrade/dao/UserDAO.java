package com.studentgrade.dao;

import com.studentgrade.database.DatabaseConnection;
import com.studentgrade.exception.DatabaseException;
import com.studentgrade.model.Role;
import com.studentgrade.model.User;
import java.sql.*;

public class UserDAO {
    
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password); // Simplified for this demo
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role"))
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error authenticating user.", e);
        }
        return null;
    }
}
