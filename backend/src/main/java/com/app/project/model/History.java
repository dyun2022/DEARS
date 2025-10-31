package com.app.project.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name = "History")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private int history_id;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "record", nullable = false)
    private String record;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    // empty constructor for JPA
    public History() {}

    public History(Date date, String record, Pet pet) {

        this.date = date;
        this.record = record;
        this.pet = pet;
    }

    // Getters and Setters
    public Date getEntryDate() {
        return date;
    }
    public String getRecord() { return record; }
    public int getHistoryId() { return history_id; }
}