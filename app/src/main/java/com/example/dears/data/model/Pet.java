package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Pet model. Should match the backend's Pet.
 */
public class Pet implements Serializable {
        private static final long serialVersionUID = 1L;
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

        public void setPetID(int petID) {
                this.petID = petID;
        }

        public void setName(String name) {
                this.name = name;
        }

        public void setType(String type) {
                this.type = type;
        }

        public void setUser(User user) {
                this.user = user;
        }

        public void setAge(AgeStage age) {
                this.age = age;
        }

        public void setGrowthPoints(int growthPoints) {
                this.growthPoints = growthPoints;
        }

        public void setHunger(Hunger hunger) {
                this.hunger = hunger;
        }

        public void setHungerMeter(int hungerMeter) {
                this.hungerMeter = hungerMeter;
        }

        public void setHappiness(Happiness happiness) {
                this.happiness = happiness;
        }

        public void setHappinessMeter(int happinessMeter) {
                this.happinessMeter = happinessMeter;
        }

        public void setEnergy(Energy energy) {
                this.energy = energy;
        }

        public void setEnergyMeter(int energyMeter) {
                this.energyMeter = energyMeter;
        }

        public void setFull(boolean full) {
                this.full = full;
        }

        public void setHappy(boolean happy) {
                this.happy = happy;
        }

        public void setReadyToGrow(boolean readyToGrow) {
                this.readyToGrow = readyToGrow;
        }
}
