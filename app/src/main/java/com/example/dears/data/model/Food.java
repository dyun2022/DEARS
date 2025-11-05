package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Food implements Serializable {

    @SerializedName("food_id")
    private int foodID;

    @SerializedName("type")
    private String type;

    @SerializedName("food_points")
    private int foodPoints;

    // Empty constructor (needed for Retrofit/Gson)
    public Food() {
    }

    public Food(int foodID, String type, int foodPoints) {
        this.foodID = foodID;
        this.type = type;
        this.foodPoints = foodPoints;
    }

    // Getters and Setters
    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFoodPoints() {
        return foodPoints;
    }

    public void setFoodPoints(int foodPoints) {
        this.foodPoints = foodPoints;
    }
}