package com.app.project.repository;

import com.app.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    User findByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users WHERE user_id = :id", nativeQuery = true)
    void deleteUserById(@Param("id") int id);
}