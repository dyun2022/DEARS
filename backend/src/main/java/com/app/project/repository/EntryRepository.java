package com.app.project.repository;

import com.app.project.model.Entry;
import com.app.project.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Integer> {
    Optional<Pet> findByUser(int user_id);
}