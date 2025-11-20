package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Happiness AgeStage. Should match the backend's AgeStage.
 */
public class AgeStage implements Serializable {
    @SerializedName("ageID")
    private int ageID;

    @SerializedName("ageStage")
    private String ageStage;

    @SerializedName("meterMax")
    private int meterMax;

    @SerializedName("pets")
    private List<Pet> pets = new ArrayList<>();

    public AgeStage() {};

    public AgeStage(int aid, String as, int mm) {
        ageID = aid;
        ageStage = as;
        meterMax = mm;
    }
    public int getAgeID() {
        return ageID;
    }

    public String getAgeStage() {
        return ageStage;
    }

    public int getMeterMax() {
        return meterMax;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setAgeStage(String age) {
        this.ageStage = age;
    }

    public void setAgeID(int i) {
        this.ageID = i;
    }
}
