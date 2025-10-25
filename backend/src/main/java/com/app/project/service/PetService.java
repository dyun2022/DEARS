package com.app.project.service;

import com.app.project.model.Pet;
import com.app.project.model.User;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.app.project.service.PetService;

@Service
public interface PetService {
    public Optional<Pet> getPetByID(int pet_id);
    public Optional<Pet> getPetByUserID(int user_id);
    public Pet createPet(int user_id, Pet pet);
    public Pet updatePet(int pet_id, Pet updatedPet);
}
