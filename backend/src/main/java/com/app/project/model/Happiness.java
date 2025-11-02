package com.app.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "Happiness")
public class Happiness {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "happiness_id")
    private int happiness_id;

    @OneToOne
    @JoinColumn(name = "age_id", nullable = false)
    @JsonIgnore
    private AgeStage age;

    @Column(name = "meter_max", nullable = false)
    private int meter_max;

    // empty constructor for JPA
    public Happiness() {}

    public Happiness(AgeStage age, int meter_max) {
        this.age = age;
        this.meter_max = meter_max;
    }

    // Getters and Setters
    public int getHappinessID() {
        return happiness_id;
    }
    public void setHappinessID(int happiness_id) {
        this.happiness_id = happiness_id;
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
    public void setMeterMax(int meter_max) {
        this.meter_max = meter_max;
    }
}