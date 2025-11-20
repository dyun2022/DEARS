package com.app.project.controller;

import com.app.project.DearsApplication;
import com.app.project.model.*;
import com.app.project.repository.*;
import com.app.project.service.PetService;
import com.app.project.service.UserService;
import com.app.project.service.impl.AgeStageServiceImpl;
import com.app.project.service.impl.EnergyServiceImpl;
import com.app.project.service.impl.HappinessServiceImpl;
import com.app.project.service.impl.HungerServiceImpl;
import com.app.project.service.impl.PetServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DearsApplication.class)
@AutoConfigureMockMvc
public class PetGrowthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserService userService;

    @Mock
    private AgeStageRepository ageStageRepository;
    @Mock
    private HungerRepository hungerRepository;
    @Mock
    private HappinessRepository happinessRepository;
    @Mock
    private EnergyRepository energyRepository;

    @InjectMocks
    private PetController petController;

    Pet pet;
    User user;

    @BeforeEach
    public void setup() {
        // Setup user
        user = new User();
        user.setUserID(1);

        // Setup initial pet
        AgeStage baby = new AgeStage("baby", 10);
        baby.setAgeID(1);
        Hunger hunger = new Hunger(baby, 10);
        Happiness happy = new Happiness(baby, 10);
        Energy energy = new Energy(baby, 10);

        pet = new Pet();
        pet.setPetID(1);
        pet.setName("Fluffy");
        pet.setType("Cat");
        pet.setAge(baby);
        pet.setHunger(hunger);
        pet.setHungerMeter(5);
        pet.setHappiness(happy);
        pet.setHappinessMeter(5);
        pet.setEnergy(energy);
        pet.setEnergyMeter(5);
        pet.setGrowthPoints(0);

        // Default stubs
        when(userService.getUserById(1)).thenReturn(user);
        when(petRepository.findById(1)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        when(ageStageRepository.findById(1)).thenReturn(Optional.of(baby));
        when(hungerRepository.findById(1)).thenReturn(Optional.of(hunger));
        when(happinessRepository.findById(1)).thenReturn(Optional.of(happy));
        when(energyRepository.findById(1)).thenReturn(Optional.of(energy));
    }

    // BBT1: growthPoints increase accordingly but pet doesn't grow yet
    @Test
    public void testPetGrowthWhenMetersIncrease() throws Exception {
        // Set meters high to trigger growth
        pet.setHungerMeter(8);
        pet.setHappinessMeter(8);
        pet.setEnergyMeter(8);
        petController.checkMeters(pet);
        Pet updated = (Pet) petRepository.save(pet);
        assertEquals(5, updated.getGrowthPoints());
    }

    // BBT2: pet grows to next stage
    @Test
    public void testCheckGrowthMovesToNextAge() throws Exception {
        // Ratios all >= 0.75
        AgeStage teen = new AgeStage("teen", 20);
        teen.setAgeID(2);
        when(ageStageRepository.findById(any())).thenReturn(Optional.of(teen));

        pet.setHungerMeter(10);
        pet.setHappinessMeter(10);
        pet.setEnergyMeter(10);
        petController.checkMeters(pet);
        Pet updated = (Pet) petRepository.save(pet);
        updated.setHungerMeter(10);
        updated.setHappinessMeter(10);
        updated.setEnergyMeter(10);
        petController.checkMeters(updated);
        Pet finalUpdated = (Pet) petRepository.save(updated);

        assertEquals(0, finalUpdated.getGrowthPoints());
        assertEquals(2, finalUpdated.getAge().getAgeID());
    }

    // BBT3: pet stays at max age if at max age even after meters are increased
    @Test
    public void testCheckGrowthStopsAtMaxAge() throws Exception {
        AgeStage finalStage = new AgeStage("adult", 40);
        finalStage.setAgeID(3);
        when(ageStageRepository.findById(3))
                .thenReturn(Optional.of(finalStage));

        pet.setAge(finalStage);

        pet.setHungerMeter(40);
        pet.setHappinessMeter(40);
        pet.setEnergyMeter(40);
        petController.checkMeters(pet);
        Pet updated = (Pet) petRepository.save(pet);
        assertEquals(3, updated.getAge().getAgeID());
    }

    // BBT4: meters don't increase if one meter isn't 0.75 or above
    @Test
    public void testMetersNoIncrementIfOneLow() throws Exception {
        pet.setHungerMeter(10);
        pet.setHappinessMeter(4); // < 0.75
        pet.setEnergyMeter(10);

        petController.checkMeters(pet);
        Pet updated = (Pet) petRepository.save(pet);

        assertEquals(0, updated.getGrowthPoints());
    }

    // BBT5: meters reset if pet grows
    @Test
    public void testCheckGrowthResetsMeters() throws Exception {
        AgeStage teen = new AgeStage("teen", 20);
        teen.setAgeID(2);
        when(ageStageRepository.findById(any())).thenReturn(Optional.of(teen));

        pet.setHungerMeter(10);
        pet.setHappinessMeter(10);
        pet.setEnergyMeter(10);
        petController.checkMeters(pet);
        Pet updated = (Pet) petRepository.save(pet);
        updated.setHungerMeter(10);
        updated.setHappinessMeter(10);
        updated.setEnergyMeter(10);
        petController.checkMeters(updated);
        Pet finalUpdated = (Pet) petRepository.save(updated);

        assertEquals(0, finalUpdated.getGrowthPoints());
        assertEquals(2, finalUpdated.getAge().getAgeID());
        assertEquals(0, finalUpdated.getHungerMeter());
        assertEquals(0, finalUpdated.getHappinessMeter());
        assertEquals(0, finalUpdated.getEnergyMeter());
    }

    // WB1: checkGrowth resets all meters exactly when growth triggers
    @Test
    public void testCheckGrowthResetsMetersOnThreshold() {
        AgeStage teen = new AgeStage("teen", 20);
        teen.setAgeID(2);
        when(ageStageRepository.findById(2)).thenReturn(Optional.of(teen));

        pet.setGrowthPoints(10); // >= baby.meterMax
        petController.checkGrowth(pet);

        // Verify age advanced
        assertEquals(teen, pet.getAge());
        // Verify meters reset
        assertEquals(0, pet.getHungerMeter());
        assertEquals(0, pet.getHappinessMeter());
        assertEquals(0, pet.getEnergyMeter());
        // Verify growth points reset
        assertEquals(0, pet.getGrowthPoints());
    }

    // WB2: checkGrowth does nothing if at max age
    @Test
    public void testCheckGrowthDoesNothingAtMaxAge() {
        AgeStage adult = new AgeStage("adult", 40);
        adult.setAgeID(3);
        when(ageStageRepository.findById(4)).thenReturn(Optional.empty()); // next age doesn't exist

        pet.setAge(adult);
        pet.setGrowthPoints(50); // exceeds max
        petController.checkGrowth(pet);

        assertEquals(adult, pet.getAge()); // still adult
        assertEquals(50, pet.getGrowthPoints()); // unchanged
    }

    // WB3: partial meter increase does not trigger growth
    @Test
    public void testCheckMetersPartialIncreaseNoGrowth() {
        // only hunger and happiness above 0.75, energy below
        pet.setHungerMeter(8);
        pet.setHappinessMeter(8);
        pet.setEnergyMeter(2);

        petController.checkMeters(pet);

        assertEquals(0, pet.getGrowthPoints()); // no growth yet
        assertEquals(1, pet.getAge().getAgeID()); // age unchanged
    }

    // WB4: checkMeters increments growthPoints exactly by 5 when all meters >= 0.75
    @Test
    public void testCheckMetersIncrementGrowthPoints() {
        pet.setHungerMeter(8);
        pet.setHappinessMeter(8);
        pet.setEnergyMeter(8);
        int before = pet.getGrowthPoints();
        petController.checkMeters(pet);
        assertEquals(before + 5, pet.getGrowthPoints());
    }
}
