package com.app.project.repository;

import com.app.project.model.Journal;
import com.app.project.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Integer> {
    Optional<Journal> findById(int journal_id);
    Optional<Journal> findByPet(Pet pet_id);
}