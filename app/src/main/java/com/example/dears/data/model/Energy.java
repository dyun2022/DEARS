package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Energy implements Serializable {
    @SerializedName("energyID")
    private int energyID;

    @SerializedName("age")
    private AgeStage age;

    @SerializedName("meterMax")
    private int meterMax;

    public int getEnergyID() {
        return energyID;
    }

    public AgeStage getAge() {
        return age;
    }

    public int getMeterMax() {
        return meterMax;
    }
}
