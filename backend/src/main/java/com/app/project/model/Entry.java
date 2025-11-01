package com.app.project.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private Date date;

    @Column(name = "summary", nullable = false)
    private String summary;

    @Column(name = "mood", nullable = false)
    private String mood;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    // empty constructor for JPA
    public Entry() {}

    public Entry(Date date) {
        this.date = date;
    }

    // Getters and Setters
    public Date getEntryDate() {
        return date;
    }
    public String getSummary() { return summary; }
    public String getMood() { return mood; }
    public int getEntryId() { return entry_id; }
}