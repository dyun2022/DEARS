package com.app.project.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name = "Pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private int pet_id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "age_id", nullable = false)
    private AgeStage age;

    @Column(name = "growth_points", nullable = false)
    private int growth_points;

    @ManyToOne
    @JoinColumn(name = "hunger_id", nullable = false)
    private Hunger hunger;

    @Column(name = "hunger_meter", nullable = false)
    private int hunger_meter;

    @ManyToOne
    @JoinColumn(name = "happiness_id", nullable = false)
    private Happiness happiness;

    @Column(name = "happiness_meter", nullable = false)
    private int happiness_meter;

    @ManyToOne
    @JoinColumn(name = "energy_id", nullable = false)
    private Energy energy;

    @Column(name = "energy_meter", nullable = false)
    private int energy_meter;


    // empty constructor for JPA
    public Pet() {}

    public Pet(User user, AgeStage age, String type, String name, int growth_points, int hunger_meter, int happiness_meter, int energy_meter) {
        this.user = user;
        this.age = age;
        this.type = type;
        this.name = name;
        this.growth_points = growth_points;
        this.hunger_meter = hunger_meter;
        this.happiness_meter = happiness_meter;
        this.energy_meter = energy_meter;
    }

    // Getters and Setters
    public int getPetID() {
        return pet_id;
    }
    public void setPetID(int pet_id) {
        this.pet_id = pet_id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Happiness getHappiness() {
        return happiness;
    }
    public void setHappiness(Happiness happiness) {
        this.happiness = happiness;
    }
    public int getHappinessMeter() {
        return happiness_meter;
    }
    public void setHappinessMeter(int happiness_meter) {
        this.happiness_meter = happiness_meter;
    }
    public Hunger getHunger() {
        return hunger;
    }
    public void setHunger(Hunger hunger) {
        this.hunger = hunger;
    }
    public int getHungerMeter() {
        return hunger_meter;
    }
    public void setHungerMeter(int hunger_meter) {
        this.hunger_meter = hunger_meter;
    }
    public Energy getEnergy() {
        return energy;
    }
    public void setEnergy(Energy energy) {
        this.energy = energy;
    }
    public int getEnergyMeter() {
        return energy_meter;
    }
    public void setEnergyMeter(int energy_meter) {
        this.energy_meter = energy_meter;
    }
    public AgeStage getAge() {
        return age;
    }
    public void setAge(AgeStage age) {
        this.age = age;
    }
    public int getGrowthPoints() {
        return growth_points;
    }
    public void setGrowthPoints(int growth_points) {
        this.growth_points = growth_points;
    }

    public boolean isReadyToGrow() {
        return growth_points >= age.getMeterMax();
    }

    public boolean isHappy() {
        return happiness_meter >= happiness.getMeterMax();
    }

    public boolean isFull() {
        return hunger_meter >= hunger.getHungerID();
    }

    public boolean isEnergy() {
        return energy_meter >= energy.getMeterMax();
    }
}