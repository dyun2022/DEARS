package com.app.project.controller;

import com.app.project.model.User;
import com.app.project.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty() ||
                user.getBirthday() == null || user.getBirthday().toString().isEmpty() ||
                user.getAvatar() == null || user.getAvatar().isEmpty()) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "All fields are required");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            if (userService.existsByUsername(user.getUsername())) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Username already taken");
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
            User savedUser = userService.saveUser(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to register user: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Username and password are required");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            User user = userService.findByUsername(username);
            if (user != null && user.verify(username, password)) {
                Map<String, Object> response = new HashMap<>();
                response.put("userID", user.getUserID());
                response.put("username", user.getUsername());
                response.put("message", "Login successful");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid username/email or password");
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Login failed: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user){
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
    }

    @GetMapping
    public List<User> getAllUser(){
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") int userID){
        return new ResponseEntity<>(userService.getUserById(userID), HttpStatus.OK);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> editPassword(@PathVariable("id") int userID, @RequestBody Map<String, String> request) {
        try {
            User user = userService.getUserById(userID);
            if (user == null) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            String password = request.get("password");
            if (password == null || password.isEmpty()) return new ResponseEntity<>("Password is required", HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>(userService.updatePassword(userID, password), HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error updating password: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/birthday")
    public ResponseEntity<?> editBirthday(@PathVariable("id") int userID, @RequestBody Map<String, LocalDate> request) {
        try {
            User user = userService.getUserById(userID);
            if (user == null) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            LocalDate birthday = request.get("birthday");
            if (birthday == null) return new ResponseEntity<>("Birthday is required", HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>(userService.updateBirthday(userID, birthday), HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error updating birthday: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/avatar")
    public ResponseEntity<?> editAvatar(@PathVariable("id") int userID, @RequestBody Map<String, String> request) {
        try {
            User user = userService.getUserById(userID);
            if (user == null) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            String avatar = request.get("avatar");
            if (avatar == null || avatar.isEmpty()) return new ResponseEntity<>("Avatar is required", HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>(userService.updateAvatar(userID, avatar), HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error updating avatar: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/username")
    public ResponseEntity<?> editUsername(@PathVariable("id") int userID,
                                          @RequestBody Map<String, String> request) {
        try {
            User user = userService.getUserById(userID);
            if (user == null) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            String username = request.get("username");
            if (username == null || username.isBlank()) {
                return new ResponseEntity<>("Username is required", HttpStatus.BAD_REQUEST);
            }

            if (username.equals(user.getUsername())) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }

            if (userService.existsByUsername(username)) {
                return new ResponseEntity<>("Username already taken", HttpStatus.CONFLICT);
            }

            User updated = userService.updateUsername(userID, username);
            return new ResponseEntity<>(updated, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error updating username: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
