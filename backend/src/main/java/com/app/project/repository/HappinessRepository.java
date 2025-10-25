package com.app.project.repository;

import com.app.project.model.Happiness;
import com.app.project.model.AgeStage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HappinessRepository extends JpaRepository<Happiness, Integer> {
    Happiness findByAge(AgeStage age);
    AgeStage findByID(int id);
}