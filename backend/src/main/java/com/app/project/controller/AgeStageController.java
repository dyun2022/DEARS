package com.app.project.controller;

import com.app.project.model.AgeStage;
import com.app.project.service.AgeStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/age")
public class AgeStageController {
    @Autowired
    private AgeStageService ageStageService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> geAgeById(@PathVariable int id) {
        AgeStage age = ageStageService.getAgeById(id);
        if (age != null) {
            Map<String, Object> ageDto = new HashMap<>();
            ageDto.put("age_id", age.getAgeID());
            ageDto.put("age", age.getAgeStage());
            ageDto.put("meter_max", age.getMeterMax());
            return new ResponseEntity<>(ageDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllAges() {
        List<AgeStage> ages = ageStageService.getAllAges();

        List<Map<String, Object>> ageDtos = ages.stream()
                .map(age -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("age_id", age.getAgeID());
                    dto.put("age", age.getAgeStage());
                    dto.put("meter_max", age.getMeterMax());
                    return dto;
                })
                .toList();

        return new ResponseEntity<>(ageDtos, HttpStatus.OK);
    }
}