package com.app.project.service.impl;

import com.app.project.model.AgeStage;
import com.app.project.model.Pet;
import com.app.project.model.User;
import com.app.project.repository.AgeStageRepository;
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

    public Optional<Pet> getPetByID(int pet_id) {
        return petRepository.findById(pet_id);
    }

    @Override
    public Optional<Pet> getPetByUserID(int userID) {
        return petRepository.findByUserUserID(userID);
    }
    public Pet createPet(int user_id, AgeStage age, String type, String name, int growthPoints, int hungerMeter, int happinessMeter, int energyMeter) {
        // find user by userID
        User user = userRepository.findById(user_id).orElseThrow(() -> new RuntimeException("User not found"));
        AgeStage ageStage = ageStageRepository.findById(1).orElseThrow(() -> new RuntimeException("Age not found"));

        Pet pet = new Pet(user, ageStage, type, name, 0, 0, 0, 0);

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