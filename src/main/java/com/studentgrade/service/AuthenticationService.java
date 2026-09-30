package com.studentgrade.service;

import com.studentgrade.dao.UserDAO;
import com.studentgrade.exception.AuthenticationException;
import com.studentgrade.model.User;

public class AuthenticationService {
    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be empty.");
        }

        User user = userDAO.authenticate(username, password);
        if (user == null) {
            throw new AuthenticationException("Invalid username or password.");
        }
        
        return user;
    }
}
