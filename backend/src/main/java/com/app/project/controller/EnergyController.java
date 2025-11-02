package com.app.project.controller;

import com.app.project.model.Energy;
import com.app.project.service.EnergyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/energy")
public class EnergyController {
    @Autowired
    private EnergyService energyService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getEnergyById(@PathVariable int id) {
        Energy energy = energyService.getEnergyById(id);
        if (energy != null) {
            Map<String, Object> energyDto = new HashMap<>();
            energyDto.put("energy_id", energy.getEnergyID());
            energyDto.put("age_id", energy.getAge());
            energyDto.put("meter_max", energy.getMeterMax());
            return new ResponseEntity<>(energyDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllEnergy() {
        List<Energy> energies = energyService.getAllEnergy();

        List<Map<String, Object>> energyDtos = energies.stream()
                .map(energy -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("energy_id", energy.getEnergyID());
                    dto.put("age_id", energy.getAge());
                    dto.put("meter_max", energy.getMeterMax());
                    return dto;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(energyDtos, HttpStatus.OK);
    }
}