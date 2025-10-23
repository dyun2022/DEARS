package com.app.project.service.impl;

import com.app.project.model.Food;
import com.app.project.repository.FoodRepository;
import com.app.project.service.FoodService;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {
    @Autowired
    private FoodRepository foodRepository;

    @Override
    public Food getFoodById(int id) {
        return foodRepository.findById(id).orElseThrow(
                () -> new RunTimeException("Food not found with id: " + id)
        );
    }

    @Override
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    @PostConstruct
    public void init() {
        if (foodRepository.count() == 0) {
            foodRepository.save(new Food("Tree Bark", 5));
            foodRepository.save(new Food("Berries", 10));
            foodRepository.save(new Food("Mushroom", 20));
            foodRepository.save(new Food("Honey", 5));
            foodRepository.save(new Food("Salmon", 20));
        }
    }
}