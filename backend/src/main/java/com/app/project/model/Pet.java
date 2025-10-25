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

    @ManyToOne
    @JoinColumn(name = "hunger_id", nullable = false)
    private Hunger hunger;

    @ManyToOne
    @JoinColumn(name = "happiness_id", nullable = false)
    private Happiness happiness;

    @ManyToOne
    @JoinColumn(name = "energy_id", nullable = false)
    private Energy energy;

    // empty constructor for JPA
    public Pet() {}

    public Pet(User user, String type, String name) {
        this.user = user;
        this.name = name;
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
    public Hunger getHunger() {
        return hunger;
    }
    public void setHunger(Hunger hunger) {
        this.hunger = hunger;
    }
    public Energy getEnergy() {
        return energy;
    }
    public void setEnergy(Energy energy) {
        this.energy = energy;
    }
    public AgeStage getAge() {
        return age;
    }
    public void setAge(AgeStage age) {
        this.age = age;
    }
}