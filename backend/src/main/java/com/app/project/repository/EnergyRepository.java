package com.app.project.repository;

import com.app.project.model.Energy;
import com.app.project.model.AgeStage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnergyRepository extends JpaRepository<Energy, Integer> {
    Energy findByAge(AgeStage age);
    AgeStage findByID(int id);
}