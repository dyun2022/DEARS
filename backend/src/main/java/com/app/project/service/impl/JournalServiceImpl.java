package com.app.project.service.impl;

import com.app.project.model.Entry;
import com.app.project.model.Journal;
import com.app.project.model.Pet;
import com.app.project.repository.EntryRepository;
import com.app.project.repository.JournalRepository;
import com.app.project.service.JournalService;
import com.app.project.service.PetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class JournalServiceImpl implements JournalService {
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private PetService petService;

    @Override
    public Journal createJournal() {
        Journal journal = new Journal();
        return journalRepository.save(journal);
    }

    @Override
    public Journal getJournalById(int journal_id) {
        Optional<Journal> journal = journalRepository.findById(journal_id);
        return journal.orElse(null);
    }

    @Override
    public ArrayList<Entry> getAllEntries() {
        List<Entry> entries = entryRepository.findAll();
        if (entries == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(entries);
    }

    @Override
    public Entry getEntrybyId(int entry_id) {
        Optional<Entry> entry = entryRepository.findById(entry_id);
        return entry.orElse(null);
    }

    public List<Entry> getEntriesByDate(LocalDate date) {
        List<Entry> entries = entryRepository.findByDate(date);
        if (entries == null) {
            return new ArrayList<>();
        }
        return entries;
    }

    public Journal getJournalByPetId(int petId) {
        // Pet p = this.getPetById(petId);
        return journalRepository.findByPet_petId(petId).orElse(null);
    }



    public Pet getPetById(int petId) {
        return petService.getPetByID(petId).orElse(null);
    }

    public Journal saveJournal(Journal journal) {
        return journalRepository.save(journal);
    }
}