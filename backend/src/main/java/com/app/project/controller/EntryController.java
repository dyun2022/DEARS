package com.app.project.controller;

import com.app.project.model.Entry;
import com.app.project.model.Journal;
import com.app.project.model.Pet;
import com.app.project.service.EntryService;
import com.app.project.service.JournalService;
import com.app.project.service.PetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/entry")
public class EntryController {
    @Autowired
    private EntryService entryService;

    @Autowired
    private PetService petService;

    @Autowired
    private JournalService journalService;

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

    @GetMapping("/date/{date}/{petId}/{journalId}")
    public ResponseEntity<Map<String, Object>> getEntriesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable int petId, @PathVariable int journalId) {

        // retrieve entries for that date
        List<Entry> entries = entryService.getEntryByDate(date);

        if (entries != null && !entries.isEmpty()) {
            List<Map<String, Object>> responseList = new ArrayList<>();

            // Map each Entry object to the desired response format (DTO)
            for (Entry entry : entries) {
                Map<String, Object> entryDto = new HashMap<>();
                entryDto.put("entry_id", entry.getEntryId());
                entryDto.put("summary", entry.getSummary());
                entryDto.put("date", entry.getEntryDate());
                entryDto.put("mood", entry.getMood());
                entryDto.put("pet_id", entry.getPet().getPetID());
                entryDto.put("journal", entry.getJournalId());
                responseList.add(entryDto);
            }

            for (Map<String, Object> rl : responseList) {
                if (rl.get("pet_id").equals(petId) && rl.get("journal").equals(journalId)) {
                    System.out.print(rl);
                    return new ResponseEntity<>(rl, HttpStatus.OK);
                }
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create/{date}")
    public ResponseEntity<Map<String, Object>> createEntry(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody Map<String, String> entryData) {
        try {
            // extract data from entryData
            String mood = entryData.get("mood");
            String summary = entryData.get("summary");
            String petIdStr = entryData.get("pet_id");
            String journalIdStr = entryData.get("journal_id");

            // check for null values
            if (petIdStr == null || petIdStr.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "pet_id is required"));
            }

            if (journalIdStr == null || journalIdStr.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "journal_id is required"));
            }

            // parse ints
            int petId = Integer.parseInt(petIdStr);
            int journalId = Integer.parseInt(journalIdStr);

            // Get entities
            Pet pet = entryService.getPetById(petId);
            if (pet == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Pet not found with id: " + petId));
            }

            Journal journal = journalService.getJournalById(journalId);
            if (journal == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Journal not found with id: " + journalId));
            }

            Entry entry = new Entry();
            entry.setEntryDate(date);
            entry.setMood(mood);
            entry.setSummary(summary);
            entry.setPet(pet);
            entry.setJournal(journal);

            Entry savedEntry = entryService.saveEntry(entry);

            Map<String, Object> response = new HashMap<>();
            response.put("date", date);
            response.put("mood", mood);
            response.put("summary", summary);
            response.put("pet_id", petId);
            response.put("journal_id", journalId);
            response.put("entry_id", savedEntry.getEntryId());

            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid number format for pet_id or journal_id"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }

    }

    @PatchMapping("/update/{date}")
    public ResponseEntity<Map<String, Object>> updateEntry(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody Map<String, String> entryData) {
        try {
            String petIdStr = entryData.get("pet_id");
            String journalIdStr = entryData.get("journal_id");
            String mood = entryData.get("mood");
            String summary = entryData.get("summary");

            if (petIdStr == null || petIdStr.isEmpty() || journalIdStr == null || journalIdStr.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "pet_id and journal_id are required in the request body for lookup."));
            }

            int petId = Integer.parseInt(petIdStr);
            int journalId = Integer.parseInt(journalIdStr);

            // get the entry to be updated
            Entry existingEntry = entryService.findByDateAndPet_pet_idAndJournal_journal_id(date, petId, journalId);

            if (existingEntry == null) {
                return new ResponseEntity<>(
                        Map.of("error", "Entry not found for Date: " + date + ", Pet ID: " + petId + ", Journal ID: " + journalId),
                        HttpStatus.NOT_FOUND
                );
            }

            if (mood != null) {
                existingEntry.setMood(mood);
            }
            if (summary != null) {
                existingEntry.setSummary(summary);
            }

            Entry updatedEntry = entryService.saveEntry(existingEntry);

            Map<String, Object> response = new HashMap<>();
            response.put("entry_id", updatedEntry.getEntryId());
            response.put("date", updatedEntry.getEntryDate());
            response.put("mood", updatedEntry.getMood());
            response.put("summary", updatedEntry.getSummary());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }

}
