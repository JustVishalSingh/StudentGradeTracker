package com.studentgrade.database;

import com.studentgrade.exception.DatabaseException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new DatabaseException("Unable to find config.properties in resources folder.");
            }
            properties.load(input);
            // Ensure driver is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.err.println("Database configuration error: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
            );
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to the database. Please verify your credentials and ensure MySQL is running.", e);
        }
    }
}
