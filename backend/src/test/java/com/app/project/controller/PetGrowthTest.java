package com.app.project.controller;

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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PetGrowthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PetRepository petRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AgeStageRepository ageStageRepository;
    @MockitoBean
    private HungerRepository hungerRepository;
    @MockitoBean
    private HappinessRepository happinessRepository;
    @MockitoBean
    private EnergyRepository energyRepository;

    private Pet pet;
    private User user;

    @BeforeEach
    public void setup() {
        // Setup user
        user = new User();
        user.setUserID(1);

        // Setup initial pet
        AgeStage age = new AgeStage("baby", 10);
        age.setAgeID(1);
        Hunger hunger = new Hunger(age, 10);
        Happiness happy = new Happiness(age, 10);
        Energy energy = new Energy(age, 10);

        pet = new Pet();
        pet.setPetID(1);
        pet.setName("Fluffy");
        pet.setType("Cat");
        pet.setAge(age);
        pet.setHunger(hunger);
        pet.setHungerMeter(5);
        pet.setHappiness(happy);
        pet.setHappinessMeter(5);
        pet.setEnergy(energy);
        pet.setEnergyMeter(5);
        pet.setGrowthPoints(0);

        // Default stubs
        Mockito.when(userService.getUserById(1)).thenReturn(user);
        Mockito.when(petRepository.findById(1)).thenReturn(Optional.of(pet));
        Mockito.when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        Mockito.when(ageStageRepository.findById(1)).thenReturn(Optional.of(age));
        Mockito.when(hungerRepository.findById(1)).thenReturn(Optional.of(hunger));
        Mockito.when(happinessRepository.findById(1)).thenReturn(Optional.of(happy));
        Mockito.when(energyRepository.findById(1)).thenReturn(Optional.of(energy));

        // Stub next stage for growth check
        AgeStage teenAge = new AgeStage("teen", 20);
        teenAge.setAgeID(2);
        Mockito.when(ageStageRepository.findById(2)).thenReturn(Optional.of(teenAge));
        Mockito.when(hungerRepository.findById(2)).thenReturn(Optional.of(new Hunger(teenAge, 20)));
        Mockito.when(happinessRepository.findById(2)).thenReturn(Optional.of(new Happiness(teenAge, 20)));
        Mockito.when(energyRepository.findById(2)).thenReturn(Optional.of(new Energy(teenAge, 20)));
    }

    // BBT1: meters increase but pet doesn't grow yet
    @Test
    public void testPetGrowthWhenMetersHigh() throws Exception {
        // Set meters high to trigger growth
        pet.setHungerMeter(8);
        pet.setHappinessMeter(8);
        pet.setEnergyMeter(8);

        mockMvc.perform(patch("/api/pet/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new java.util.HashMap<String, Object>() {{
                                    put("hunger_meter", 8);
                                    put("happiness_meter", 8);
                                    put("energy_meter", 8);
                                }}
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.growthPoints").value(5))
                .andExpect(jsonPath("$.age.ageID").value(1));
    }

    // BBT2: meters increase
    @Test
    public void testCheckMetersIncreasesGrowthPoints() throws Exception {
        // 75% threshold based on meterMax = 10
        pet.setHungerMeter(8);
        pet.setHappinessMeter(8);
        pet.setEnergyMeter(8);
        pet.setGrowthPoints(0);

        mockMvc.perform(patch("/api/pet/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new java.util.HashMap<String, Object>() {{
                                    put("hunger_meter", 8);
                                    put("happiness_meter", 8);
                                    put("energy_meter", 8);
                                }}
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.growthPoints").value(5));    // ✔ checkMeters()
    }

    // BBT3: pet grows to next stage
    @Test
    public void testCheckGrowthMovesToNextAge() throws Exception {
        // Ratios all >= 0.75
        pet.setHungerMeter(10);
        pet.setHappinessMeter(10);
        pet.setEnergyMeter(10);
        pet.setGrowthPoints(5);   // Enough to grow because meterMax = 10

        mockMvc.perform(patch("/api/pet/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new java.util.HashMap<String, Object>() {{
                                    put("hunger_meter", 10);
                                    put("happiness_meter", 10);
                                    put("energy_meter", 10);
                                }}
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age.ageID").value(2)) // ✔ moved to next stage
                .andExpect(jsonPath("$.growthPoints").value(0)); // ✔ reset by checkGrowth()
    }

    // BBT4: pet stays at max age if at max age
    @Test
    public void testCheckGrowthStopsAtMaxAge() throws Exception {
        AgeStage finalStage = new AgeStage("adult", 40);
        finalStage.setAgeID(3);
        Mockito.when(ageStageRepository.findById(3))
                .thenReturn(Optional.of(finalStage));

        pet.setAge(finalStage);

        mockMvc.perform(patch("/api/pet/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new java.util.HashMap<String, Object>() {{
                                    put("hunger_meter", 10);
                                    put("happiness_meter", 10);
                                    put("energy_meter", 10);
                                }}
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age.ageID").value(3)) // ✔ stays final
                .andExpect(jsonPath("$.growthPoints").value(5)); // ✔ never reset
    }

    // BBT5: meters don't increase if one meter isn't 0.75 or above
    @Test
    public void testCheckMetersNoIncrementIfOneLow() throws Exception {
        pet.setHungerMeter(10);
        pet.setHappinessMeter(4); // < 0.75
        pet.setEnergyMeter(10);
        pet.setGrowthPoints(0);

        mockMvc.perform(patch("/api/pet/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new java.util.HashMap<String, Object>() {{
                                    put("hunger_meter", 10);
                                    put("happiness_meter", 4);
                                    put("energy_meter", 10);
                                }}
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.growthPoints").value(0)); // ✔ no increment
    }

    // BBT6: meters reset if pet grows
    @Test
    public void testCheckGrowthResetsMeters() throws Exception {
        pet.setHungerMeter(10);
        pet.setHappinessMeter(10);
        pet.setEnergyMeter(10);
        pet.setGrowthPoints(10); // enough to evolve

        mockMvc.perform(patch("/api/pet/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new java.util.HashMap<String, Object>() {{
                                    put("hunger_meter", 10);
                                    put("happiness_meter", 10);
                                    put("energy_meter", 10);
                                }}
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age.ageID").value(2))
                .andExpect(jsonPath("$.hungerMeter").value(0))      // ✔ reset
                .andExpect(jsonPath("$.happinessMeter").value(0))   // ✔ reset
                .andExpect(jsonPath("$.energyMeter").value(0));     // ✔ reset
    }

}
