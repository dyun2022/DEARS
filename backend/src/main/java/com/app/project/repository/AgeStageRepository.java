package com.app.project.repository;

import com.app.project.model.AgeStage;
import com.app.project.model.Energy;
import com.app.project.model.Happiness;
import com.app.project.model.Hunger;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgeStageRepository extends JpaRepository<AgeStage, Integer> {
    AgeStage findByAgeStage(String ageStage);
}