package com.app.project.service;

import com.app.project.model.AgeStage;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface AgeStageService {
    AgeStage getAgeById(int id);
    List<AgeStage> getAllAges();
    int getAgeMeter(AgeStage age);
}