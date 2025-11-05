package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Happiness AgeStage. Should match the backend's AgeStage.
 */
public class AgeStage implements Serializable {
    @SerializedName("age_id")
    private int age_id;

    @SerializedName("age_stage")
    private String age_stage;

    @SerializedName("meter_max")
    private int meter_max;

    @SerializedName("pets")
    private List<Pet> pets = new ArrayList<>();


    public String getAge_stage() {
            return age_stage;
    }

    public int getMeter_max() {
        return meter_max;
    }
}
