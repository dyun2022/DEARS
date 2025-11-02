package com.app.project.repository;

import com.app.project.model.Entry;
import com.app.project.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Integer> {
    List<Entry> findByDate(LocalDate date);
}