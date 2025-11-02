package com.app.project.controller;

import com.app.project.model.AgeStage;
import com.app.project.model.Energy;
import com.app.project.model.Happiness;
import com.app.project.model.Hunger;
import com.app.project.model.Pet;
import com.app.project.model.Food;
import com.app.project.repository.AgeStageRepository;
import com.app.project.repository.EnergyRepository;
import com.app.project.repository.FoodRepository;
import com.app.project.repository.HappinessRepository;
import com.app.project.repository.HungerRepository;
import com.app.project.repository.PetRepository;
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

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private AgeStageRepository ageStageRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    HappinessRepository happinessRepository;

    @Autowired
    HungerRepository hungerRepository;

    @Autowired
    EnergyRepository energyRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetByID(@PathVariable("id") int pet_id) {
        Optional<Pet> pet = petService.getPetByID(pet_id);
        return pet.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity<Pet> getPetByUserID(@PathVariable("userID") int userID) {
        Optional<Pet> pet = petService.getPetByUserID(userID);
        return pet.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/user/{user_id}")
    public ResponseEntity<?> createPet(@PathVariable("user_id") int user_id, @RequestBody Pet petRequest) {

        String name = petRequest.getName();
        String type = petRequest.getType();

        if (name == null || name.isEmpty() || type == null || type.isEmpty()) {
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

        // Check if user already has a pet
        if (owner.getPet() != null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User already has a pet");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        // create pet
        Optional<AgeStage> ageStage = ageStageRepository.findById(1);
        if (ageStage.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Age not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        AgeStage age = ageStage.get();
        Optional<Hunger> hungerStage = hungerRepository.findById(1);
        if (hungerStage.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Hunger not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        Hunger hunger = hungerStage.get();
        Optional<Happiness> happyStage = happinessRepository.findById(1);
        if (happyStage.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Happy not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        Happiness happy = happyStage.get();
        Optional<Energy> energyStage = energyRepository.findById(1);
        if (energyStage.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Energy not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        Energy energy = energyStage.get();
        Pet newPet = petService.createPet(user_id, age, type, name, 0, hunger,0, happy, 0, energy, 0);
        if (newPet == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create pet");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(newPet, HttpStatus.CREATED);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable("id") int petId,
                                        @RequestBody Map<String, Object> updates) {
        Pet existingPet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name": existingPet.setName((String)value); break;
                case "hunger": existingPet.setHunger((Hunger)value); break;
                case "hunger_meter": existingPet.setHungerMeter((int) value); break;
                case "happiness": existingPet.setHappiness((Happiness)value); break;
                case "happiness_meter": existingPet.setHappinessMeter((int) value); break;
                case "energy": existingPet.setEnergy((Energy)value); break;
                case "energy_meter": existingPet.setEnergyMeter((int) value); break;
                case "growthPoints": existingPet.setGrowthPoints((int) value); break;
                case "ageStage": existingPet.setAge((AgeStage)value); break;
            }
        });

        Pet updatedPet = petRepository.save(existingPet);
        return ResponseEntity.ok(updatedPet);
    }

    @PatchMapping("/{id}/feed/{food_id}")
    public ResponseEntity<?> feedPet(@PathVariable("id") int pet_id, @PathVariable("food_id") int food_id) {
        Pet pet = petRepository.findById(pet_id).orElseThrow(() -> new RuntimeException("Pet not found"));
        Food food = foodRepository.findById(food_id).orElseThrow(() -> new RuntimeException("Food not found"));

        int newHunger = pet.getHungerMeter() + food.getFoodPoints();
        if (newHunger >= pet.getHunger().getMeterMax()) {
            pet.setHungerMeter(pet.getHunger().getMeterMax());
        }
        else {
            pet.setHungerMeter(newHunger);
        }

        checkMeters(pet);

        Pet updated = petRepository.save(pet);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/sleep")
    public ResponseEntity<?> sleepPet(@PathVariable("id") int pet_id, @PathVariable("energy_id") int energy_id) {
        Pet pet = petRepository.findById(pet_id).orElseThrow(() -> new RuntimeException("Pet not found"));
        Energy energy = energyRepository.findById(energy_id).orElseThrow(() -> new RuntimeException("Energy not found"));

        int newEnergy = pet.getEnergyMeter() + 5;
        if (newEnergy >= pet.getEnergy().getMeterMax()) {
            pet.setEnergyMeter(pet.getEnergy().getMeterMax());
        }
        else {
            pet.setEnergyMeter(newEnergy);
        }

        checkMeters(pet);

        Pet updated = petRepository.save(pet);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/chat")
    public ResponseEntity<?> chatPet(@PathVariable("id") int pet_id, @PathVariable("happiness_id") int happiness_id) {
        Pet pet = petRepository.findById(pet_id).orElseThrow(() -> new RuntimeException("Pet not found"));
        Happiness happy_id = happinessRepository.findById(happiness_id).orElseThrow(() -> new RuntimeException("Happiness not found"));

        int newHappy = pet.getHappinessMeter() + 5;
        if (newHappy >= pet.getHappiness().getMeterMax()) {
            pet.setHappinessMeter(pet.getHappiness().getMeterMax());
        }
        else {
            pet.setHappinessMeter(newHappy);
        }

        checkMeters(pet);

        Pet updated = petRepository.save(pet);
        return ResponseEntity.ok(updated);
    }

    // if reached growth meter max, update all meter maxes
    private void checkGrowth(Pet pet) {
        AgeStage currAge = pet.getAge();
        Happiness currHappy = pet.getHappiness();
        Hunger currHunger = pet.getHunger();
        Energy currEnergy = pet.getEnergy();
        if (pet.getGrowthPoints() >= pet.getAge().getMeterMax()) {
            int nextAge = currAge.getAgeID() + 1;
            ageStageRepository.findById(nextAge).ifPresent(nextStage -> {
                pet.setAge(nextStage);
                pet.setGrowthPoints(0);
            });
            int nextHappy = currHappy.getHappinessID() + 1;
            happinessRepository.findById(nextHappy).ifPresent(nextHappyStage -> {
                pet.setHappiness(nextHappyStage);
                pet.setHappinessMeter(0);
            });
            int nextHunger = currHunger.getHungerID() + 1;
            hungerRepository.findById(nextHunger).ifPresent(nextHungerStage -> {
                pet.setHunger(nextHungerStage);
                pet.setHungerMeter(0);
            });
            int nextEnergy = currEnergy.getEnergyID() + 1;
            energyRepository.findById(nextEnergy).ifPresent(nextEnergyStage -> {
                pet.setEnergy(nextEnergyStage);
                pet.setEnergyMeter(0);
            });
        }
    }

    private void checkMeters(Pet pet) {
        double hungerRatio = (double) pet.getHungerMeter() / pet.getHunger().getMeterMax();
        double happyRatio = (double) pet.getHappinessMeter() / pet.getHappiness().getMeterMax();
        double energyRatio = (double) pet.getEnergyMeter() / pet.getEnergy().getMeterMax();

        if (hungerRatio >= 0.75 && happyRatio >= 0.75 && energyRatio >= 0.75) {
            pet.setGrowthPoints(pet.getGrowthPoints() + 5);
            checkGrowth(pet);
        }

        Pet updated = petRepository.save(pet);
    }
}