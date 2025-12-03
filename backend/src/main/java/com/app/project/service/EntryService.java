package com.app.project.service;

import com.app.project.model.Entry;
import com.app.project.model.Pet;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public interface EntryService {
    Entry getEntryById(int id);
    Entry saveEntry(Entry entry);
    Entry createEntry(Map<String, String> entryData);
    Pet getPetById(int petId);

    List<Entry> getEntryByDate(LocalDate date);

    Entry findByDateAndPet_pet_idAndJournal_journal_id(LocalDate date, int petId, int journalId);}
