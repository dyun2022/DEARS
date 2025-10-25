package com.app.project.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "AgeStage")
public class AgeStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "age_id")
    private int age_id;

    @Column(name = "age_stage", nullable = false)
    private String age_stage;

    @Column(name = "meter_max", nullable = false)
    private int meter_max;

    @OneToMany(mappedBy = "age_stage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

    // empty constructor for JPA
    public AgeStage() {}

    public AgeStage(String age_stage, int meter_max) {
        this.age_stage = age_stage;
        this.meter_max = meter_max;
    }

    // Getters and Setters
    public int getAgeID() {
        return age_id;
    }
    public void setAgeID(int age_id) {
        this.age_id = age_id;
    }
    public String getAgeStage() {
        return age_stage;
    }
    public void setAgeStage(String age_stage) {
        this.age_stage = age_stage;
    }
    public int getMeterMax() {
        return meter_max;
    }
    public void setMeterMax(int meter_max) {
        this.meter_max = meter_max;
    }
    public List<Pet> getPets() {
        return pets;
    }
    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }
}