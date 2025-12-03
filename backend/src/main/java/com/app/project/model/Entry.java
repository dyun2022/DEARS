package com.app.project.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name = "Entry")
public class Entry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private int entry_id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "summary", nullable = false)
    private String summary;

    @Column(name = "mood", nullable = false)
    private String mood;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    @JsonIgnore
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "journal")
    @JsonIgnore
    private Journal journal;


    // empty constructor for JPA
    public Entry() {}

    public Entry(LocalDate date) {
        this.date = date;
    }

    // Getters and Setters
    public LocalDate getEntryDate() { return date; }
    public String getSummary() { return summary; }
    public String getMood() { return mood; }
    public int getEntryId() { return entry_id; }
    public Journal getEntryJournalId() { return journal; }
    public Pet getPet() { return pet; }

    public Integer getJournalId() {
        return journal != null ? journal.getJournalId() : null;
    }

    public void setEntryDate(LocalDate date) { this.date = date; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setMood(String mood) { this.mood = mood; }

    public void setPet(Pet pet) { this.pet = pet; }

    public void setJournal(Journal journal) { this.journal = journal; }

    public void setEntryId(int i) {  this.entry_id = i;  }
}