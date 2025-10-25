package com.app.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Hunger")
public class Hunger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hunger_id")
    private int hunger_id;

    @OneToOne
    @JoinColumn(name = "age_id", nullable = false)
    private AgeStage age;

    @Column(name = "hunger_points", nullable = false)
    private int hunger_points;

    // empty constructor for JPA
    public Hunger() {}

    public Hunger(AgeStage age, int hunger_points) {
        this.age = age;
        this.hunger_points = hunger_points;
    }

    // Getters and Setters
    public int getHungerID() {
        return hunger_id;
    }
    public void setHungerID(int hunger_id) {
        this.hunger_id = hunger_id;
    }
    public AgeStage getAge() {
        return age;
    }
    public void setAge(AgeStage age) {
        this.age = age;
    }
    public int getHungerPoints() {
        return hunger_points;
    }
    public void setHungerPoints(int hunger_points) {
        this.hunger_points = hunger_points;
    }
}