package com.app.project.controller;

import com.app.project.model.Food;
import com.app.project.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFoodById(@PathVariable int id) {
        Food food = foodService.getFoodById(id);
        if (food != null) {
            Map<String, Object> foodDto = new HashMap<>();
            foodDto.put("food_id", food.getFoodID());
            foodDto.put("food_type", food.getType());
            foodDto.put("food_points", food.getFoodPoints());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllFoods() {
        List<Food> foods = foodService.getAllFoods();
        List<Map<String, Object>> foodDtos = foods.stream()
                .map(food -> {
                    Map<String, Object> foodDto = new HashMap<>();
                    foodDto.put("food_id", food.getFoodID());
                    foodDto.put("food_type", food.getType());
                    foodDto.put("food_points", food.getFoodPoints());
                    return foodDto;
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(foodDtos, HttpStatus.OK);
    }
}