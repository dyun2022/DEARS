package com.app.project.service;

import com.app.project.model.User;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User saveUser(User user);
    List<User> getAllUsers();
    User getUserById(int id);
    boolean existsByUsername(String username);
    User findByUsername(String username);
    User updateBirthday(int userId, LocalDate birthday);
    User updatePassword(int userId, String password);
    User updateAvatar(int userId, String avatar);
    User updateUsername(int userId, String username);
}
