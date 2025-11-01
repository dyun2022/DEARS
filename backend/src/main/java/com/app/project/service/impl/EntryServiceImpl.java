package com.app.project.service.impl;

import com.app.project.model.ChatChoices;
import com.app.project.model.Entry;
import com.app.project.repository.ChatChoicesRepository;
import com.app.project.repository.EntryRepository;
import com.app.project.service.ChatChoicesService;
import com.app.project.service.EntryService;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryServiceImpl implements EntryService {
    @Autowired
    private EntryRepository entryRepository;

    @Override
    public Entry getEntryById(int id) {
        return entryRepository.findById(id).orElseThrow(() -> new RuntimeException("Entry not found with id: " + id));
    }
}