package com.app.project.service.impl;

import com.app.project.model.AgeStage;
import com.app.project.model.Happiness;
import com.app.project.repository.HappinessRepository;
import com.app.project.service.AgeStageService;
import com.app.project.service.HappinessService;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HappinessServiceImpl implements HappinessService {
    @Autowired
    private HappinessRepository happinessRepository;
    @Autowired
    private AgeStageService ageStageService;

    @Override
    public Happiness getHappyById(int id) {
        return happinessRepository.findById(id).orElseThrow(() -> new RuntimeException("Happiness not found with id: " + id));
    }

    @Override
    public List<Happiness> getAllHappy() {
        return happinessRepository.findAll();
    }

    @Override
    public int getHappyMeter(AgeStage age) {
        Happiness happy = happinessRepository.findByAge(age);
        if (happy == null) {
            throw new RuntimeException("Happiness not found for age: " + age.getAgeStage());
        }
        return happy.getHappinessPoints();
    }

    @PostConstruct
    public void init() {
        if (happinessRepository.count() == 0) {
            happinessRepository.save(new Happiness(happinessRepository.findByID(1), 5));
            happinessRepository.save(new Happiness(happinessRepository.findByID(2), 10));
            happinessRepository.save(new Happiness(happinessRepository.findByID(3), 20));
        }
    }
}