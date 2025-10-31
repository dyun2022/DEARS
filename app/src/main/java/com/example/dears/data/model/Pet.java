package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Pet model. Should match the backend's Pet.
 */
public class Pet {
        @SerializedName("pet_id")
        private int pet_id;

        @SerializedName("name")
        private String name;

        @SerializedName("type")
        private String type;

        @SerializedName("user")
        private User user;

        @SerializedName("age")
        private AgeStage age;

        @SerializedName("growth_points")
        private int growth_points;

        @SerializedName("hunger")
        private Hunger hunger;

        @SerializedName("hunger_meter")
        private int hunger_meter;

        @SerializedName("happiness")
        private Happiness happiness;

        @SerializedName("happiness_meter")
        private int happiness_meter;

        @SerializedName("energy")
        private Energy energy;

        @SerializedName("energy_meter")
        private int energy_meter;
}
