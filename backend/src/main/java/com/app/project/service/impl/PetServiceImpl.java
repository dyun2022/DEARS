package com.app.project.service.impl;

import com.app.project.model.AgeStage;
import com.app.project.model.Energy;
import com.app.project.model.Happiness;
import com.app.project.model.Hunger;
import com.app.project.model.Pet;
import com.app.project.model.User;
import com.app.project.repository.AgeStageRepository;
import com.app.project.repository.EnergyRepository;
import com.app.project.repository.HappinessRepository;
import com.app.project.repository.HungerRepository;
import com.app.project.repository.PetRepository;
import com.app.project.repository.UserRepository;
import com.app.project.service.PetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PetServiceImpl implements PetService {
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgeStageRepository ageStageRepository;

    @Autowired
    private HungerRepository hungerRepository;

    @Autowired
    private HappinessRepository happinessRepository;

    @Autowired
    private EnergyRepository energyRepository;

    public Optional<Pet> getPetByID(int pet_id) {
        return petRepository.findById(pet_id);
    }

    @Override
    public Optional<Pet> getPetByUserID(int userID) {
        return petRepository.findByUser_UserID(userID);
    }
    public Pet createPet(int user_id, AgeStage age, String type, String name, int growthPoints, Hunger hunger, int hungerMeter, Happiness happiness, int happinessMeter, Energy energy, int energyMeter) {
        // find user by userID
        User user = userRepository.findById(user_id).orElseThrow(() -> new RuntimeException("User not found"));
        AgeStage ageStage = ageStageRepository.findById(1).orElseThrow(() -> new RuntimeException("Age not found"));
        Hunger hungerStage = hungerRepository.findById(1).orElseThrow(() -> new RuntimeException("Hunger not found"));
        Happiness happinessStage = happinessRepository.findById(1).orElseThrow(() -> new RuntimeException("Happiness not found"));
        Energy energyStage = energyRepository.findById(1).orElseThrow(() -> new RuntimeException("Energy not found"));

        Pet pet = new Pet(user, ageStage, type, name, 0, hungerStage, 0, happinessStage, 0, energyStage, 0);

        return savePet(pet);
    }
    public Pet updatePet(int pet_id, Pet updatedPet) {
        return petRepository.findById(pet_id)
                .map(pet -> {
                    pet.setHappiness(updatedPet.getHappiness());
                    pet.setHunger(updatedPet.getHunger());
                    pet.setEnergy(updatedPet.getEnergy());
                    pet.setAge(updatedPet.getAge());
                    pet.setType(updatedPet.getType());
                    return petRepository.save(pet);
                })
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + pet_id));
    }

    public Pet savePet (Pet pet) {
        return petRepository.save(pet);
    }
}