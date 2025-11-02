package com.app.project.service;

import com.app.project.model.Entry;
import com.app.project.model.Food;
import com.app.project.model.Journal;
import com.app.project.model.Pet;
import com.app.project.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.app.project.service.PetService;

@Service
public interface JournalService {
    public Journal createJournal();
    Journal getJournalById(int journal_id);
    ArrayList<Entry> getAllEntries();
    Entry getEntrybyId(int entry_id);

    List<Entry> getEntriesByDate(LocalDate date);
    Pet getPetById(int petId);

    Journal saveJournal(Journal journal);
}
