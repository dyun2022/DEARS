package com.app.project.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name = "Journal")
public class Journal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "journal_id")
    private int journal_id;

    @Column(name = "entries")
    private ArrayList<Entry> entries;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    // empty constructor for JPA
    public Journal() {}

    public Journal(Pet pet) {
        this.pet = pet;
    }

    // Getters and Setters
    public ArrayList<Entry> getEntries() { return entries; }
    public Entry getEntry(int index) { return entries.get(index); }
    public int getJournalId() { return journal_id; }
}