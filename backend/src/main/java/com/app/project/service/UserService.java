package com.app.project.service;

import com.app.project.model.User;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User saveUser(User user);
    List<User> getAllUsers();
    User getUserById(int id);
    boolean existsByUsername(String username);
    User findByUsername(String username);
}