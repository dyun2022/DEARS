package com.app.project.controller;

import com.app.project.model.Pet;
import com.app.project.model.User;
import com.app.project.service.PetService;
import com.app.project.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PetService petService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Basic validation
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty() ||
                user.getBirthday() == null || user.getBirthday().toString().isEmpty() ||
                user.getAvatar() == null || user.getAvatar().isEmpty()) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "All fields are required");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            // Check if username already exists
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
            // First try to find by username
            User user = userService.findByUsername(username);

            // If user found, check password
            if (user != null && user.verify(username, password)) {
                // Create response with user info but exclude sensitive data
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
        return new ResponseEntity<User>(userService.saveUser(user), HttpStatus.CREATED);
    }

    // GetAll
    @GetMapping
    public List<User> getAllUser(){
        return userService.getAllUsers();
    }

    // Get by Id
    @GetMapping("{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") int userID){
        return new ResponseEntity<User>(userService.getUserById(userID), HttpStatus.OK);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> editPassword(@PathVariable("id") int userID, @RequestBody Map<String, String> request) {
        try {
            User user = userService.getUserById(userID);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            String password = request.get("password");
            if (password == null || password.isEmpty()) {
                return new ResponseEntity<>("Password is required", HttpStatus.BAD_REQUEST);
            }

            User updatedUser = userService.updatePassword(userID, password);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
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
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            LocalDate birthday = request.get("birthday");
            if (birthday == null) {
                return new ResponseEntity<>("Birthday is required", HttpStatus.BAD_REQUEST);
            }

            User updatedUser = userService.updateBirthday(userID, birthday);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
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
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            String avatar = request.get("avatar");
            if (avatar == null || avatar.isEmpty()) {
                return new ResponseEntity<>("Avatar is required", HttpStatus.BAD_REQUEST);
            }

            User updatedUser = userService.updateAvatar(userID, avatar);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error updating avatar: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // NEW: update username
    @PutMapping("/{id}/username")
    public ResponseEntity<?> editUsername(@PathVariable("id") int userID,
                                          @RequestBody Map<String, String> request) {
        try {
            User user = userService.getUserById(userID);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            String username = request.get("username");
            if (username == null || username.isBlank()) {
                return new ResponseEntity<>("Username is required", HttpStatus.BAD_REQUEST);
            }

            // If unchanged, return current user (no-op)
            if (username.equals(user.getUsername())) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }

            // Reject if taken by another user
            if (userService.existsByUsername(username)) {
                return new ResponseEntity<>("Username already taken", HttpStatus.CONFLICT);
            }

            User updatedUser = userService.updateUsername(userID, username);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error updating username: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
