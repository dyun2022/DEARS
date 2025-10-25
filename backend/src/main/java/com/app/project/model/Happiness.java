package com.app.project.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Happiness")
public class Happiness {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "happiness_id")
    private int happiness_id;

    @OneToOne
    @JoinColumn(name = "age_id", nullable = false)
    private AgeStage age;

    @Column(name = "happiness_points", nullable = false)
    private int happiness_points;

    // empty constructor for JPA
    public Happiness() {}

    public Happiness(AgeStage age, int happiness_points) {
        this.age = age;
        this.happiness_points = happiness_points;
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
    public int getHappinessPoints() {
        return happiness_points;
    }
    public void setHappinessPoints(int happiness_points) {
        this.happiness_points = happiness_points;
    }
}