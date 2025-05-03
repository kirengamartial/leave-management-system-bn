package com.martial.leave_service.service;

import java.util.List;

import com.martial.leave_service.model.User;

public interface UserService {
    User getUser(String userId);

    List<User> getAllUsers();

    User getUserByEmail(String email);
}