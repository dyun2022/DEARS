package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Hunger model. Should match the backend's Hunger.
 */
public class Hunger implements Serializable {
    @SerializedName("hungerID")
    private int hungerID;

    @SerializedName("age")
    private AgeStage age;

    @SerializedName("meterMax")
    private int meterMax;

    public int getHungerID() {
        return hungerID;
    }

    public AgeStage getAge() {
        return age;
    }

    public int getMeterMax() {
        return meterMax;
    }
}
