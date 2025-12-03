package com.app.project.service.impl;

import com.app.project.model.Entry;
import com.app.project.model.Pet;
import com.app.project.repository.EntryRepository;
import com.app.project.service.EntryService;
import com.app.project.service.PetService;

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

    @Override
    public List<Entry> getEntryByDate(LocalDate date) {
        return entryRepository.findByDate(date);
    }

    @Override
    public Entry findByDateAndPet_pet_idAndJournal_journal_id(LocalDate date, int petId, int journalId) {
        return entryRepository.findByDateAndPet_petIdAndJournal_journalId(date, petId, journalId);

    }
}