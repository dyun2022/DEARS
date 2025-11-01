package com.app.project.service.impl;

import com.app.project.model.AgeStage;
import com.app.project.model.Energy;
import com.app.project.model.Hunger;
import com.app.project.repository.EnergyRepository;
import com.app.project.service.AgeStageService;
import com.app.project.service.EnergyService;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnergyServiceImpl implements EnergyService {
    @Autowired
    private EnergyRepository energyRepository;
    @Autowired
    private AgeStageService ageStageService;

    @Override
    public Energy getEnergyById(int id) {
        return energyRepository.findById(id).orElseThrow(() -> new RuntimeException("Energy not found with id: " + id));
    }

    @Override
    public List<Energy> getAllEnergy() {
        return energyRepository.findAll();
    }

    @Override
    public int getEnergyMeter(AgeStage age) {
        Energy energy = energyRepository.findByAge(age);
        if (energy == null) {
            throw new RuntimeException("Energy not found for age: " + age.getAgeStage());
        }
        return energy.getMeterMax();
    }

    @PostConstruct
    public void init() {
        if (energyRepository.count() == 0) {
            List<AgeStage> ageStages = ageStageService.getAllAges(); // get all AgeStages
            for (AgeStage age : ageStages) {
                if (energyRepository.findByAge(age) == null) {
                    Energy h = new Energy(age, age.getMeterMax());
                    energyRepository.save(h);
                }
            }
        }
    }
}