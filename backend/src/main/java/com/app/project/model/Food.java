package com.app.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Food")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private int food_id;

    @Column(name = "type")
    private String type;

    @Column(name = "food_points")
    private int food_points;

    // empty constructor for JPA
    public Food() {
    }

    public Food(int food_id, String type, int food_points) {
        this.food_id = food_id;
        this.type = type;
        this.food_points = food_points;
    }

    // Getters and Setters
    public int getFoodID() {
        return food_id;
    }
    public void setFoodID(int food_id) {
        this.food_id = food_id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getFoodPoints() {
        return food_points;
    }
    public void setFoodPoints(int food_points) {
        this.food_points = food_points;
    }
}