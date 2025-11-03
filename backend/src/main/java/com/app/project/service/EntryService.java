package com.app.project.service;

import com.app.project.model.Entry;
import com.app.project.model.Food;
import com.app.project.model.Pet;
import com.app.project.model.User;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.app.project.service.PetService;

@Service
public interface EntryService {
    Entry getEntryById(int id);
}
