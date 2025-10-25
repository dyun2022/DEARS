package com.app.project.controller;

import com.app.project.model.Happiness;
import com.app.project.service.HappinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/happiness")
public class HappinessController {
    @Autowired
    private HappinessService happinessService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getHappinessById(@PathVariable int id) {
        Happiness happy = happinessService.getHappyById(id);
        if (happy != null) {
            Map<String, Object> happyDto = new HashMap<>();
            happyDto.put("happiness_id", happy.getHappinessID());
            happyDto.put("age_id", happy.getAge());
            happyDto.put("happiness_points", happy.getHappinessPoints());
            return new ResponseEntity<>(happyDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllHappy() {
        List<Happiness> happiness = happinessService.getAllHappy();

        List<Map<String, Object>> happyDtos = happiness.stream()
                .map(happy -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("happiness_id", happy.getHappinessID());
                    dto.put("age_id", happy.getAge());
                    dto.put("happiness_points", happy.getHappinessPoints());
                    return dto;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(happyDtos, HttpStatus.OK);
    }
}