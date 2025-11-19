package com.app.project.service.impl;

import com.app.project.model.ChatChoices;
import com.app.project.model.Entry;
import com.app.project.model.Pet;
import com.app.project.repository.ChatChoicesRepository;
import com.app.project.repository.EntryRepository;
import com.app.project.service.ChatChoicesService;
import com.app.project.service.EntryService;
import com.app.project.service.PetService;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class EntryServiceImpl implements EntryService {
    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private PetService petService;

    @Override
    public Entry getEntryById(int id) {
        return entryRepository.findById(id).orElseThrow(() -> new RuntimeException("Entry not found with id: " + id));
    }

    @Override
    public Entry saveEntry(Entry entry) {
        return entryRepository.save(entry);
    }

    public Pet getPetById(int petId) {
        return petService.getPetByID(petId).orElse(null);
    }

    public Entry createEntry(Map<String, String> entryData) {
        return new Entry();
    }


}