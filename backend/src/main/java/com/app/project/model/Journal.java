package com.app.project.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name = "Journal")
public class Journal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "journal_id")
    public int journalId;

    @OneToMany(mappedBy = "journal")
    @JsonManagedReference
    private List<Entry> entries = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "name", nullable = false)
    private String name;

    // empty constructor for JPA
    public Journal() {}

    public Journal(Pet pet) {
        this.pet = pet;
    }

    // Getters and Setters
    public List<Entry> getEntries() { return entries; }
    public Entry getEntry(int index) { return entries.get(index); }
    public int getJournalId() { return journalId; }
    public Pet getPet() { return pet; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public void setJournalId(int i) { this.journalId = i;  }

    public void setPet(Pet pet) { this.pet = pet;
    }
}