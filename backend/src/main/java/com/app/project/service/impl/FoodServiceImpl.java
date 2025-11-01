package com.app.project.service.impl;

import com.app.project.model.Food;
import com.app.project.repository.FoodRepository;
import com.app.project.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import jakarta.annotation.PostConstruct;

@Service
public class FoodServiceImpl implements FoodService {
    @Autowired
    private FoodRepository foodRepository;

    @Override
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    @Override
    public Food getFoodById(int id) {
        return foodRepository.findById(id).orElseThrow(() -> new RuntimeException("Food not found with id: " + id));
    }

    @PostConstruct
    public void init() {
        if (foodRepository.count() == 0) {
            foodRepository.save(new Food(0, "tree bark", 5));
            foodRepository.save(new Food(0, "berries", 10));
            foodRepository.save(new Food(0, "mushroom", 20));
            foodRepository.save(new Food(0, "honey", 5));
            foodRepository.save(new Food(0, "salmon", 20));
        }
    }
}
