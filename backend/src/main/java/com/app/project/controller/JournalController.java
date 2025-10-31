package com.app.project.controller;

import com.app.project.model.Entry;
import com.app.project.model.Journal;
import com.app.project.service.JournalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getJournalById(@PathVariable int id) {
        Journal journal = journalService.getJournalById(id);
        if (journal != null) {
            Map<String, Object> journalDto = new HashMap<>();
            journalDto.put("journal_id", journal.getJournalId());
            journalDto.put("entries", journal.getEntries());
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
                    dto.put("date", entry.getEntryDate());
                    dto.put("summary", entry.getSummary());
                    return dto;
                })
                .toList();

        return new ResponseEntity<>(entriesDtos, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getEntryByDate() {
        List<Entry> entries = journalService.getAllEntries();

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

}