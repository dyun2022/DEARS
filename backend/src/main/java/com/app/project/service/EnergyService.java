package com.app.project.service;

import com.app.project.model.Energy;
import com.app.project.model.AgeStage;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface EnergyService {
    Energy getEnergyById(int id);
    List<Energy> getAllEnergy();
    int getEnergyMeter(AgeStage age);
}