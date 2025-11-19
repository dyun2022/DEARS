package com.example.dears.data.request;

public class updatePetRequest {
    private String name;
    private int hunger_meter;
    private int happiness_meter;
    private int energy_meter;

    public updatePetRequest(String n, int hm, int ham, int em) {
        name = n;
        hunger_meter = hm;
        happiness_meter = ham;
        energy_meter = em;
    }
    public updatePetRequest(int hm, int ham, int em) {
        hunger_meter = hm;
        happiness_meter = ham;
        energy_meter = em;
    }

    public updatePetRequest(int hm, int ham) {
        hunger_meter = hm;
        happiness_meter = ham;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        updatePetRequest that = (updatePetRequest) o;

        if (hunger_meter != that.hunger_meter) return false;
        if (happiness_meter != that.happiness_meter) return false;
        if (energy_meter != that.energy_meter) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + hunger_meter;
        result = 31 * result + happiness_meter;
        result = 31 * result + energy_meter;
        return result;
    }

}
