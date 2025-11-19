package com.app.project.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.app.project.model.AgeStage;
import com.app.project.model.Energy;
import com.app.project.model.Entry;
import com.app.project.model.Happiness;
import com.app.project.model.Hunger;
import com.app.project.model.Pet;
import com.app.project.repository.JournalRepository;
import com.app.project.service.JournalService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class JournalControllerTest {
    @InjectMocks
    private JournalController controller;
    @Mock
    private JournalService service;
    @Mock
    private JournalRepository repository;

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
    void testGetJournalById() {
        ResponseEntity<Map<String, Object>> response = controller.getJournalById(1);
        assertNotNull(response);
    }

    @Test
    void testGetJournalByIdNotFound() {
        ResponseEntity<Map<String, Object>> response = controller.getJournalById(0);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCodeValue());
    }

    @Test
    void testGetAllEntries() {
        // mock entries
        Entry entry1 = new Entry();
        entry1.setEntryId(1);
        entry1.setEntryDate(LocalDate.of(2025, 11, 17));
        entry1.setSummary("Test entry 1");
        entry1.setJournalId(1);
        entry1.setPet(pet);

        Entry entry2 = new Entry();
        entry2.setEntryId(2);
        entry2.setEntryDate(LocalDate.of(2025, 11, 18));
        entry2.setSummary("Test entry 2");
        entry2.setJournalId(1);
        entry2.setPet(pet);

        ArrayList<Entry> mockEntries = new ArrayList<>();
        mockEntries.add(entry1);
        mockEntries.add(entry2);

        // mock calls
        when(service.getAllEntries()).thenReturn(mockEntries);
        ResponseEntity<List<Map<String, Object>>> response = controller.getAllEntries();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(service, times(1)).getAllEntries();
    }

    @Test
    public void testSaveEntry() {
        // mock entry
        Entry entry1 = new Entry();
        entry1.setEntryId(1);
        entry1.setEntryDate(LocalDate.of(2025, 11, 17));
        entry1.setSummary("Test entry 1");
        entry1.setJournalId(1);
        entry1.setPet(pet);
    }

  }
