package com.app.project.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.project.model.AgeStage;
import com.app.project.model.Energy;
import com.app.project.model.Entry;
import com.app.project.model.Happiness;
import com.app.project.model.Hunger;
import com.app.project.model.Journal;
import com.app.project.model.Pet;
import com.app.project.service.EntryService;
import com.app.project.service.JournalService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class EntryControllerTest {
    @InjectMocks
    private EntryController controller;
    @Mock
    private EntryService service;
    @Mock
    private JournalService journalService;

    Pet pet;
    final private int MOCK_METER_MAX = 20;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setEnergyMeter(0);
        pet.setHungerMeter(0);
        pet.setHappinessMeter(0);
        pet.setGrowthPoints(1);
        Energy e = new Energy();
        e.setMeterMax(MOCK_METER_MAX);
        Hunger h = new Hunger();
        h.setMeterMax(MOCK_METER_MAX);
        Happiness ha = new Happiness();
        ha.setMeterMax(MOCK_METER_MAX);
        AgeStage a = new AgeStage();
        a.setMeterMax(MOCK_METER_MAX);
        pet.setAge(a);
        pet.setEnergy(e);
        pet.setHunger(h);
        pet.setHappiness(ha);
    }

    @Test
    void testCreateEntry() {
        Map<String, String> entryData = Map.of(
                "mood", "happy",
                "summary", "today was fun",
                "pet_id", "1",
                "journal_id", "1"
        );

        LocalDate testDate = LocalDate.of(2025, 11, 16);

        Journal mockJournal = new Journal();
        mockJournal.setJournalId(1);
        when(journalService.getJournalById(1)).thenReturn(mockJournal);
        Pet mockPet = new Pet();
        mockPet.setPetID(1);
        when(service.getPetById(1)).thenReturn(mockPet);

        Entry savedEntry = new Entry();
        savedEntry.setEntryId(100);
        savedEntry.setEntryDate(testDate);
        savedEntry.setMood("happy");
        savedEntry.setSummary("today was fun");
        savedEntry.setPet(pet);
        savedEntry.setJournal(mockJournal);
        savedEntry.setJournalId(1);
        when(service.saveEntry(any(Entry.class))).thenReturn(savedEntry);

        ResponseEntity<Map<String, Object>> response = controller.createEntry(testDate, entryData);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(testDate, response.getBody().get("date"));
        assertEquals("happy", response.getBody().get("mood"));
        assertEquals("today was fun", response.getBody().get("summary"));
        assertEquals(1, response.getBody().get("pet_id"));
        assertEquals(1, response.getBody().get("journal_id"));
        assertEquals(100, response.getBody().get("entry_id"));

        verify(service, times(1)).getPetById(1);
        verify(journalService, times(1)).getJournalById(1);
        verify(service, times(1)).saveEntry(any(Entry.class));
    }
}