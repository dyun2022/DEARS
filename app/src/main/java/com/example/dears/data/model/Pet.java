package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Pet implements Serializable {

        @SerializedName("petID")
        private int petID;

        @SerializedName("name")
        private String name;

        @SerializedName("type")
        private String type;

        @SerializedName("user")
        private User user;

        @SerializedName("age")
        private AgeStage age;

        @SerializedName("growthPoints")
        private int growthPoints;

        @SerializedName("hunger")
        private Hunger hunger;

        @SerializedName("hungerMeter")
        private int hungerMeter;

        @SerializedName("happiness")
        private Happiness happiness;

        @SerializedName("happinessMeter")
        private int happinessMeter;

        @SerializedName("energy")
        private Energy energy;

        @SerializedName("energyMeter")
        private int energyMeter;

        @SerializedName("full")
        private boolean full;

        @SerializedName("happy")
        private boolean happy;

        @SerializedName("readyToGrow")
        private boolean readyToGrow;

        // --- Getters ---
        public int getPetID() {
                return petID;
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
        public void setAge(AgeStage newAge) {this.age = newAge;}

        public int getGrowthPoints() {
                return growthPoints;
        }

        public Hunger getHunger() {
                return hunger;
        }

        public int getHungerMeter() {
                return hungerMeter;
        }
        public void setHungerMeter(int newHunger) {this.hungerMeter = newHunger;}

        public Happiness getHappiness() {
                return happiness;
        }

        public int getHappinessMeter() {
                return happinessMeter;
        }
        public void setHappinessMeter(int newHappy) {this.happinessMeter = newHappy;}

        public Energy getEnergy() {
                return energy;
        }

        public int getEnergyMeter() {
                return energyMeter;
        }
        public void setEnergyMeter(int newEnergy) {this.energyMeter = newEnergy;}

        public void setMeters(int hunger, int happiness, int energy) {
                this.hungerMeter = hunger;
                this.happinessMeter = happiness;
                this.energyMeter = energy;
        }

        public void checkMeters() {
                if (hungerMeter >= 75 && happinessMeter >= 75 && energyMeter >= 75) {
                        growthPoints += 5;
                }

                int nextAgeID = age.getAgeID() + 1;

                // check if pet needs to evolve
                checkGrowth();
        }

        private void checkGrowth() {
                while (growthPoints >= age.getMeterMax() && age.getAgeID() < 3) {
                        growthPoints -= age.getMeterMax();

                        // reset meters after evolution
                        hungerMeter = 0;
                        happinessMeter = 0;
                        energyMeter = 0;
                }
        }

        public boolean isFull() {
                return full;
        }

        public boolean isHappy() {
                return happy;
        }

        public boolean isReadyToGrow() {
                return readyToGrow;
        }
}
