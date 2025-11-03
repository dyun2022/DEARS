package com.app.project.repository;

import com.app.project.model.Hunger;
import com.app.project.model.AgeStage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HungerRepository extends JpaRepository<Hunger, Integer> {
    Hunger findByAge(AgeStage ageStage);
}