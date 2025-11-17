package com.app.project.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.app.project.DearsApplication;
import com.app.project.model.AgeStage;
import com.app.project.model.Energy;
import com.app.project.model.Food;
import com.app.project.model.Happiness;
import com.app.project.model.Hunger;
import com.app.project.model.Pet;
import com.app.project.repository.FoodRepository;
import com.app.project.repository.PetRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@SpringBootTest(classes = DearsApplication.class)
@ExtendWith(MockitoExtension.class)
class PetControllerTest {
    @InjectMocks
    private PetController controller;

    @Mock
    private PetRepository petRepository;
    @Mock
    private FoodRepository foodRepository;

    final private int MOCK_METER_MAX = 20;

    Pet pet;


    @BeforeEach
    void setUp() {
        // Default pet. Will not grow up.
        pet = new Pet();
        pet.setEnergyMeter(0);
        pet.setHungerMeter(0);
        pet.setHappinessMeter(0);
        pet.setGrowthPoints(1);
        Energy e = new Energy();
        e.setMeterMax(MOCK_METER_MAX);
        Hunger h = new Hunger();
        h.setMeterMax(MOCK_METER_MAX);
        Happiness ha = new Happiness();
        ha.setMeterMax(MOCK_METER_MAX);
        AgeStage a = new AgeStage();
        a.setMeterMax(MOCK_METER_MAX);
        pet.setAge(a);
        pet.setEnergy(e);
        pet.setHunger(h);
        pet.setHappiness(ha);
    }

    @Test
    void getPetByID() {
    }

    @Test
    void getPetByUserID() {
    }

    @Test
    void createPet() {
    }

    @Test
    void updatePet() {
    }

    @Nested
    @DisplayName("feedPet() tests")
    class feedPetTests {
        @Test
        void feedOnce() {
            Food food = new Food(1, "food", 5);
            when(petRepository.findById(any())).thenReturn(Optional.of(pet));
            when(foodRepository.findById(any())).thenReturn(Optional.of(food));

            when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));
            ResponseEntity<?> response = controller.feedPet(1, 1);
            Pet updated = (Pet) response.getBody();

            assertEquals(5, updated.getHungerMeter());
            verify(petRepository).save(pet);
        }

        @Test
        void feedTwice() {
            Food food = new Food(1, "food", 3);
            Food food2 = new Food(2, "food", 4);
            when(petRepository.findById(any())).thenReturn(Optional.of(pet));
            when(foodRepository.findById(1)).thenReturn(Optional.of(food));
            when(foodRepository.findById(2)).thenReturn(Optional.of(food2));
            when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));
            controller.feedPet(1, 1);
            ResponseEntity<?> response = controller.feedPet(1, 2);
            Pet updated = (Pet) response.getBody();

            assertEquals(7, updated.getHungerMeter());
            verify(petRepository, atLeastOnce()).save(pet);
        }

        @Test
        void feedMax() {
            Food food = new Food(1, "food", 7);
            pet.setHungerMeter(15);
            when(petRepository.findById(any())).thenReturn(Optional.of(pet));
            when(foodRepository.findById(any())).thenReturn(Optional.of(food));
            when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));
            ResponseEntity<?> response = controller.feedPet(1,1);
            Pet updated = (Pet) response.getBody();

            assertEquals(MOCK_METER_MAX, updated.getHungerMeter());
            verify(petRepository).save(pet);
        }
    }

    @Nested
    @DisplayName("sleepPet() tests")
    class sleepPetTests {
        @Test
        void sleepOnce() {
            when(petRepository.findById(any())).thenReturn(Optional.of(pet));
            when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));
            ResponseEntity<?> response = controller.sleepPet(1);
            Pet updated = (Pet) response.getBody();

            assertEquals(5, updated.getEnergyMeter());
            verify(petRepository).save(pet);
        }

        @Test
        void sleepTwice() {
            when(petRepository.findById(any())).thenReturn(Optional.of(pet));
            when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));
            controller.sleepPet(1);
            ResponseEntity<?> response = controller.sleepPet(1);
            Pet updated = (Pet) response.getBody();

            assertEquals(10, updated.getEnergyMeter());
            verify(petRepository, atLeastOnce()).save(pet);
        }

        @Test
        void sleepMax() {
            pet.setEnergyMeter(19);
            when(petRepository.findById(any())).thenReturn(Optional.of(pet));
            when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));
            ResponseEntity<?> response = controller.sleepPet(1);
            Pet updated = (Pet) response.getBody();

            assertEquals(MOCK_METER_MAX, updated.getEnergyMeter());
            verify(petRepository).save(pet);
        }
    }

    @Test
    void chatPet() {
    }
}