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
}
