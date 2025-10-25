package com.app.project.service.impl;

import com.app.project.model.AgeStage;
import com.app.project.repository.AgeStageRepository;
import com.app.project.service.AgeStageService;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgeStageServiceImpl implements AgeStageService {
    @Autowired
    private AgeStageRepository ageStageRepository;

    @Override
    public AgeStage getAgeById(int id) {
        return ageStageRepository.findById(id).orElseThrow(() -> new RuntimeException("Age not found with id: " + id));
    }

    @Override
    public List<AgeStage> getAllAges() {
        return ageStageRepository.findAll();
    }

    @Override
    public int getAgeMeter(AgeStage age) {
        return age.getMeterMax();
    }

    @PostConstruct
    public void init() {
        if (ageStageRepository.count() == 0) {
            ageStageRepository.save(new AgeStage("baby", 10));
            ageStageRepository.save(new AgeStage("teen", 20));
            ageStageRepository.save(new AgeStage("adult", 40));
        }
    }
}