package com.app.project.repository;

import com.app.project.model.Entry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Integer> {
    List<Entry> findByDate(LocalDate date);

    Entry findByDateAndPet_petIdAndJournal_journalId(LocalDate date, int pet_id, int journal_id);
}