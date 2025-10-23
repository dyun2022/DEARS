package com.app.project.service;

import com.app.project.model.Food;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface FoodService {
    List<Food> getAllFoods();
    Food getFoodById(int id);
}