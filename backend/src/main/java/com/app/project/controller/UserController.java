package com.app.project.controller;

import com.app.project.model.Pet;
import com.app.project.model.User;
import com.app.project.service.PetService;
import com.app.project.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
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
            User user = userService.getUserByUsername(usernameOrEmail);

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

    //GetAll Rest Api
    @GetMapping
    public List<User> getAllUser(){
        return userService.getAllUsers();
    }

    //Get by Id Rest Api
    @GetMapping("{id}")
    // localhost:8080/api/Users/1
    public ResponseEntity<User> getUserById(@PathVariable("id") int userID){
        return new ResponseEntity<User>(userService.getUserByID(userID),HttpStatus.OK);
    }
}

