package com.app.project.controller;

import com.app.project.model.Hunger;
import com.app.project.service.HungerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hunger")
public class HungerController {
    @Autowired
    private HungerService hungerService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getHungerById(@PathVariable int id) {
        Hunger hunger = hungerService.getHungerById(id);
        if (hunger != null) {
            Map<String, Object> hungerDto = new HashMap<>();
            hungerDto.put("hunger_id", hunger.getHungerID());
            hungerDto.put("age_id", hunger.getAge());
            hungerDto.put("meter_max", hunger.getMeterMax());
            return new ResponseEntity<>(hungerDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllHunger() {
        List<Hunger> hungers = hungerService.getAllHunger();

        List<Map<String, Object>> hungerDtos = hungers.stream()
                .map(hunger -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("hunger_id", hunger.getHungerID());
                    dto.put("age_id", hunger.getAge());
                    dto.put("meter_max", hunger.getMeterMax());
                    return dto;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(hungerDtos, HttpStatus.OK);
    }
}