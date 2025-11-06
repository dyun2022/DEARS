package com.app.project.service.impl;

import com.app.project.model.User;
import com.app.project.repository.UserRepository;
import com.app.project.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User updateBirthday(int userId, LocalDate birthday) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBirthday(birthday);
        return userRepository.save(user);
    }

    @Override
    public User updatePassword(int userId, String password) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(password);
        return userRepository.save(user);
    }

    @Override
    public User updateAvatar(int userId, String avatar) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatar(avatar);
        return userRepository.save(user);
    }

    @Override
    public User updateUsername(int userId, String username) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(username);
        return userRepository.save(user);
    }
}
