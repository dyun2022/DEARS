package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Happiness AgeStage. Should match the backend's AgeStage.
 */
public class AgeStage {
    @SerializedName("age_id")
    private int age_id;

    @SerializedName("age_stage")
    private String age_stage;

    @SerializedName("meter_max")
    private int meter_max;

    @SerializedName("pets")
    private List<Pet> pets = new ArrayList<>();

}
