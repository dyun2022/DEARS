package com.app.project.service.impl;

import com.app.project.model.AgeStage;
import com.app.project.model.Hunger;
import com.app.project.repository.HungerRepository;
import com.app.project.service.AgeStageService;
import com.app.project.service.HungerService;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HungerServiceImpl implements HungerService {
    @Autowired
    private HungerRepository hungerRepository;
    @Autowired
    private AgeStageService ageStageService;

    @Override
    public Hunger getHungerById(int id) {
        return hungerRepository.findById(id).orElseThrow(() -> new RuntimeException("Hunger not found with id: " + id));
    }

    @Override
    public List<Hunger> getAllHunger() {
        return hungerRepository.findAll();
    }

    @Override
    public int getHungerMeter(AgeStage age) {
        Hunger hunger = hungerRepository.findByAge(age);
        if (hunger == null) {
            throw new RuntimeException("Hunger not found for age: " + age.getAgeStage());
        }
        return hunger.getMeterMax();
    }

    @PostConstruct
    public void init() {
        if (hungerRepository.count() == 0) {
            hungerRepository.save(new Hunger(hungerRepository.findByID(1), 10));
            hungerRepository.save(new Hunger(hungerRepository.findByID(2), 20));
            hungerRepository.save(new Hunger(hungerRepository.findByID(3), 40));
        }
    }
}