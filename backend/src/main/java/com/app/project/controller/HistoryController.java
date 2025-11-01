package com.app.project.controller;

import com.app.project.model.AgeStage;
import com.app.project.model.ChatChoices;
import com.app.project.model.Energy;
import com.app.project.model.Entry;
import com.app.project.model.Happiness;
import com.app.project.model.History;
import com.app.project.model.Hunger;
import com.app.project.model.Pet;
import com.app.project.model.Food;
import com.app.project.repository.AgeStageRepository;
import com.app.project.repository.ChatChoicesRepository;
import com.app.project.repository.EnergyRepository;
import com.app.project.repository.FoodRepository;
import com.app.project.repository.HappinessRepository;
import com.app.project.repository.HungerRepository;
import com.app.project.repository.EntryRepository;
import com.app.project.repository.PetRepository;
import com.app.project.service.ChatChoicesService;
import com.app.project.service.EntryService;
import com.app.project.service.HistoryService;
import com.app.project.service.PetService;
import com.app.project.service.UserService;
import com.app.project.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    @Autowired
    private HistoryService historyService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEntryById(@PathVariable int id) {
        History history = historyService.getHistoryById(id);
        if (history != null) {
            Map<String, Object> entryDto = new HashMap<>();
            entryDto.put("entry_id", history.getHistoryId());
            entryDto.put("summary", history.getRecord());
            entryDto.put("date", history.getEntryDate());
            return new ResponseEntity<>(entryDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}