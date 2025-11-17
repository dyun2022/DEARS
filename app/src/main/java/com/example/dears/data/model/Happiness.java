package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Happiness model. Should match the backend's Happiness.
 */
public class Happiness implements Serializable {
    @SerializedName("happinessID")
    private int happinessID;

    @SerializedName("age")
    private AgeStage age;

    @SerializedName("meterMax")
    private int meterMax;

    public Happiness() {};

    public Happiness(int mm) {
        meterMax = mm;
    }

    public int getHappinessID() {
        return happinessID;
    }

    public AgeStage getAge() {
        return age;
    }

    public int getMeterMax() {
        return meterMax;
    }
}
