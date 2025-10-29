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

    @Column(name = "meter_max", nullable = false)
    private int meter_max;

    // empty constructor for JPA
    public Hunger() {}

    public Hunger(AgeStage age, int meter_max) {
        this.age = age;
        this.meter_max = meter_max;
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
    public int getMeterMax() {
        return meter_max;
    }
    public void setHungerPoints(int meter_max) {
        this.meter_max = meter_max;
    }
}