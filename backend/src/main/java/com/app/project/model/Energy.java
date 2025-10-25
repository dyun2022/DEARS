package com.app.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Energy")
public class Energy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "energy_id")
    private int energy_id;

    @OneToOne
    @JoinColumn(name = "age_id", nullable = false)
    private AgeStage age;

    @Column(name = "energy_points", nullable = false)
    private int energy_points;

    // empty constructor for JPA
    public Energy() {}

    public Energy(AgeStage age, int energy_points) {
        this.age = age;
        this.energy_points = energy_points;
    }

    // Getters and Setters
    public int getEnergyID() {
        return energy_id;
    }
    public void setEnergyID(int energy_id) {
        this.energy_id = energy_id;
    }
    public AgeStage getAge() {
        return age;
    }
    public void setAge(AgeStage age) {
        this.age = age;
    }
    public int getEnergyPoints() {
        return energy_points;
    }
    public void setEnergyPoints(int energy_points) {
        this.energy_points = energy_points;
    }
}