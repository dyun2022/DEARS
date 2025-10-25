package com.app.project.service;

import com.app.project.model.Hunger;
import com.app.project.model.AgeStage;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface HungerService {
    Hunger getHungerById(int id);
    List<Hunger> getAllHunger();
    int getHungerMeter(AgeStage age);
}