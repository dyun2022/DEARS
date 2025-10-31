package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Happiness model. Should match the backend's Happiness.
 */
public class Happiness {
    @SerializedName("happiness_id")
    private int happiness_id;

    @SerializedName("age")
    private AgeStage age;

    @SerializedName("meter_max")
    private int meter_max;
}
