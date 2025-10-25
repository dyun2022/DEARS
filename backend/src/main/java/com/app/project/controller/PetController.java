package com.app.project.controller;

import com.app.project.model.Pet;
import com.app.project.service.PetService;
import com.app.project.service.UserService;
import com.app.project.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pet")
public class PetController {
    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetByID(@PathVariable("id") int pet_id) {
        Optional<Pet> pet = petService.getPetByID(pet_id);
        return pet.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<Pet> getPetByUserID(@PathVariable("user_id") int user_id) {
        Optional<Pet> pet = petService.getPetByUserID(user_id);
        return pet.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/user/{user_id}")
    public ResponseEntity<?> createPet(@PathVariable("user_id") int user_id, @RequestBody Pet petRequest) {

        String name = petRequest.getName();
        if (name == null || name.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "All fields are required");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        // Check if user exists
        User owner = userService.getUserById(user_id);
        if (owner == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found with ID: " + user_id);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // create pet
        Pet newPet = petService.createPet(user_id, name);
        if (newPet == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create pet");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(newPet, HttpStatus.CREATED);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable("id") int pet_id, @RequestBody Pet updatedPet) {
        Pet updated = petService.updatePet(pet_id, updatedPet);
        return ResponseEntity.ok(updated);
    }
}