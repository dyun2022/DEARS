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

    @Column(name = "meter_max", nullable = false)
    private int meter_max;

    // empty constructor for JPA
    public Energy() {}

    public Energy(AgeStage age, int meter_max) {
        this.age = age;
        this.meter_max = meter_max;
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
    public int getMeterMax() {
        return meter_max;
    }
    public void setEnergyPoints(int meter_max) {
        this.meter_max = meter_max;
    }
}