package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Energy implements Serializable {
    @SerializedName("energy_id")
    private int energy_id;

    @SerializedName("age")
    private AgeStage age;

    @SerializedName("meter_max")
    private int meter_max;
}
