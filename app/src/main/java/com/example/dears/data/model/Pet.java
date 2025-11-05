package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Pet model. Should match the backend's Pet.
 */
public class Pet implements Serializable {
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


        public int getPet_id() {
                return pet_id;
        }
        public String getName() {
                return name;
        }
        public String getType() {
                return type;
        }
        public User getUser() {
                return user;
        }
        public AgeStage getAge() {
                return age;
        }
        public int getGrowth_points() {
                return growth_points;
        }
        public Hunger getHunger() {
                return hunger;
        }
        public int getHunger_meter() {
                return hunger_meter;
        }
        public Happiness getHappiness() {
                return happiness;
        }
        public int getHappiness_meter() {
                return happiness_meter;
        }
        public Energy getEnergy() {
                return energy;
        }
        public int getEnergy_meter() {
                return energy_meter;
        }
}

