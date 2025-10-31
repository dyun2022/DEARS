package com.app.project.controller;

import com.app.project.model.Entry;
import com.app.project.service.EntryService;
import com.app.project.service.PetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/entry")
public class EntryController {
    @Autowired
    private EntryService entryService;

    @Autowired
    private PetService petService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEntryById(@PathVariable("id") int id) {
        Entry entry = entryService.getEntryById(id);
        if (entry != null) {
            Map<String, Object> entryDto = new HashMap<>();
            entryDto.put("entry_id", entry.getEntryId());
            entryDto.put("summary", entry.getSummary());
            entryDto.put("date", entry.getEntryDate());
            entryDto.put("mood", entry.getMood());
            return new ResponseEntity<>(entryDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{date}")
    public ResponseEntity<Map<String, Object>> createEntry(@PathVariable Date date, @RequestBody Map<String, String> entryData) {
        //  get info
        String text = entryData.get("text");
        String mood = entryData.get("mood");
        String summary = entryData.get("summary");

        // update to database
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("date", date);
        response.put("mood", mood);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}