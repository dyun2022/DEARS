package com.app.project.service;

import com.app.project.model.Happiness;
import com.app.project.model.AgeStage;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface HappinessService {
    Happiness getHappyById(int id);
    List<Happiness> getAllHappy();
    int getHappyMeter(AgeStage age);
}