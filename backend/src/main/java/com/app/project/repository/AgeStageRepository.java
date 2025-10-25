package com.app.project.repository;

import com.app.project.model.AgeStage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgeStageRepository extends JpaRepository<AgeStage, Integer> {
    AgeStage findByAgeStage(String age_stage);
}