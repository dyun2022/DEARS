package com.app.project.repository;

import com.app.project.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {
    Optional<Pet> findByUser_UserID(@Param("userID") int userID);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM pet WHERE pet_id = :id", nativeQuery = true)
    void deletePetById(@Param("id") int id);

}