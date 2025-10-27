package com.app.project.service;

import com.app.project.model.ChatChoices;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface ChatChoicesService {
    ChatChoices getChoiceById(int id);
    List<ChatChoices> getAllChoices();
}