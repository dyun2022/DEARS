package com.app.project.repository;

import com.app.project.model.ChatChoices;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatChoicesRepository extends JpaRepository<ChatChoices, Integer> {

}