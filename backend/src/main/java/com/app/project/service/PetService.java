package com.app.project.service;

import com.app.project.model.AgeStage;
import com.app.project.model.Energy;
import com.app.project.model.Happiness;
import com.app.project.model.Hunger;
import com.app.project.model.Pet;
import com.app.project.model.User;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.app.project.service.PetService;

@Service
public interface PetService {
    public Optional<Pet> getPetByID(int pet_id);
    public Optional<Pet> getPetByUserID(int userID);
    public Pet createPet(int user_id, AgeStage age, String type, String name, int growthPoints, Hunger hunger, int hungerMeter, Happiness happiness, int happinessMeter, Energy energy, int energyMeter);
    public Pet updatePet(int pet_id, Pet updatedPet);
    void deletePet(int pet_id);

}
