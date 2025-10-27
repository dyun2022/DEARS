package com.app.project.service.impl;

import com.app.project.model.ChatChoices;
import com.app.project.repository.ChatChoicesRepository;
import com.app.project.service.ChatChoicesService;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatChoicesServiceImpl implements ChatChoicesService {
    @Autowired
    private ChatChoicesRepository chatChoicesRepository;

    @Override
    public ChatChoices getChoiceById(int id) {
        return chatChoicesRepository.findById(id).orElseThrow(() -> new RuntimeException("Choice not found with id: " + id));
    }

    @Override
    public List<ChatChoices> getAllChoices() {
        return chatChoicesRepository.findAll();
    }

    @PostConstruct
    public void init() {
        if (chatChoicesRepository.count() == 0) {
            chatChoicesRepository.save(new ChatChoices("Hello!"));
            chatChoicesRepository.save(new ChatChoices("How are you?"));
            chatChoicesRepository.save(new ChatChoices("Tell me a joke!"));
        }
    }
}