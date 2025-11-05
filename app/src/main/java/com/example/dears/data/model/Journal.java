package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;

public class Journal implements Serializable {
    @SerializedName("entry_id")
    private int entry_id;
    @SerializedName("pet_id")
    private int pet_id;

    @SerializedName("journal_id")
    private int journal_id;
    @SerializedName("date")
    private LocalDate date;
    @SerializedName("summary")
    private String summary;

    public Journal() {}

    // Getters
    public int getEntryId() {
        return entry_id;
    }

    public int getJournalId() {
        return journal_id;
    }

    public int getPetId() {
        return pet_id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getSummary() {
        return summary;
    }

    public void setEntryId(int entry_id) {
        this.entry_id = entry_id;
    }

    public void setPetId(int pet_id) {
        this.pet_id = pet_id;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
