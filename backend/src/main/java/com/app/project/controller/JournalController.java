package com.app.project.controller;

import com.app.project.model.Entry;
import com.app.project.model.Journal;
import com.app.project.model.Pet;
import com.app.project.service.JournalService;
import com.app.project.service.PetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/journal")
public class JournalController {
    @Autowired
    private JournalService journalService;
    @Autowired
    private PetService petService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getJournalById(@PathVariable int id) {
        Journal journal = journalService.getJournalById(id);
        if (journal != null) {
            Map<String, Object> journalDto = new HashMap<>();
            journalDto.put("journal_id", journal.getJournalId());

            List<Map<String, Object>> entriesDtos = journal.getEntries().stream()
                    .map(entry -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("entry_id", entry.getEntryId());
                        dto.put("date", entry.getEntryDate());
                        dto.put("summary", entry.getSummary());
                        return dto;
                    })
                    .toList();

            journalDto.put("entries", entriesDtos);
            return new ResponseEntity<>(journalDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllEntries() {
        List<Entry> entries = journalService.getAllEntries();

        List<Map<String, Object>> entriesDtos = entries.stream()
                .map(entry -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("entry_id", entry.getEntryId());
                    dto.put("pet_id", entry.getPet().getPetID());
                    dto.put("journal_id", entry.getJournalId());
                    dto.put("date", entry.getEntryDate());
                    dto.put("summary", entry.getSummary());
                    return dto;
                })
                .toList();

        return new ResponseEntity<>(entriesDtos, HttpStatus.OK);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Map<String, Object>>> getEntryByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Entry> entries = journalService.getEntriesByDate(date);

        List<Map<String, Object>> entriesDtos = entries.stream()
                .map(entry -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("entry_id", entry.getEntryId());
                    dto.put("date", entry.getEntryDate());
                    dto.put("summary", entry.getSummary());
                    return dto;
                })
                .toList();

        return new ResponseEntity<>(entriesDtos, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createJournal(@RequestBody Map<String, Object> journalData) {
        try {
            Journal journal = new Journal();

            // link to pet
            if (journalData != null && journalData.containsKey("pet_id")) {
                Number petIdNum = (Number) journalData.get("pet_id");
                int petId = petIdNum.intValue();
                Pet pet = journalService.getPetById(petId);
                if (pet == null) {
                    return new ResponseEntity<>(Map.of("error", "Pet not found"), HttpStatus.NOT_FOUND);
                }
                journal = new Journal(pet);
            }
            journal.setName("Daily Journal");

            Journal savedJournal = journalService.saveJournal(journal);

            Map<String, Object> response = new HashMap<>();
            response.put("journal_id", savedJournal.getJournalId());
            response.put("pet_id", savedJournal.getPet() != null ? savedJournal.getPet().getPetID() : null);
            response.put("name", savedJournal.getName());
            response.put("entries", new ArrayList<>());

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Map.of("error", "Failed to create journal"), HttpStatus.BAD_REQUEST);
        }
    }

}