package com.example.dears;

import static org.junit.Assert.*;
import com.example.dears.data.model.Pet;


import org.junit.Before;
import org.junit.Test;

public class PetLevelUpTest {
    private Pet pet;

    @Before
    public void setUp() {
        pet = new Pet();
    }

    // Test 1: all meters below 75 -> no growth
    @Test
    public void testMetersBelowThreshold() {
        pet.setMeters(50, 50, 50);

        assertEquals(0, pet.getGrowthPoints());
        assertEquals(0, pet.getAge().getAgeID());
    }

    // Test 2: all meters are 75 -> growth +5
    @Test
    public void testMetersAtThreshold() {
        pet.setMeters(75, 75, 75);

        assertEquals(5, pet.getGrowthPoints());
        assertEquals(0, pet.getAge().getAgeID());
    }

    // Test 3: single level up occurs
    @Test
    public void testSingleLevelUp() {
        pet.setMeters(75, 75, 75);
        pet.checkMeters(); // +5
        pet.setMeters(75, 75, 75); // +5 -> level up from baby to teen

        assertEquals(1, pet.getAge().getAgeID());
        assertEquals(0, pet.getGrowthPoints());
    }

    // Test 4: multiple level ups
    @Test
    public void testMultipleLevelUps() {
        pet.setMeters(75, 75, 75);
        pet.checkMeters(); // +5
        pet.setMeters(75, 75, 75);
        pet.checkMeters(); // +5 -> level up from baby to teen

        pet.setMeters(75, 75, 75);
        pet.checkMeters(); // +5
        pet.setMeters(75, 75, 75);
        pet.checkMeters(); // +5
        pet.setMeters(75, 75, 75);
        pet.checkMeters(); // +5
        pet.setMeters(75, 75, 75);
        pet.checkMeters(); // +5 -> level up from teen to adult

        assertEquals(3, pet.getAge().getAgeID());
        assertEquals(0, pet.getGrowthPoints());
    }

    // Test 5: Max level reached, can't evolve further
    @Test
    public void testMaxLevelReached() {
        for (int i = 0; i < 14; i++) {
            pet.setMeters(75, 75, 75);

            pet.checkMeters();
        }

        assertEquals(3, pet.getAge().getAgeID());
    }
}
