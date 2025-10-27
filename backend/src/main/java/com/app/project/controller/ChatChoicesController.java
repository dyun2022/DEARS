package com.app.project.controller;

import com.app.project.model.ChatChoices;
import com.app.project.service.ChatChoicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatChoicesController {
    @Autowired
    private ChatChoicesService chatChoicesService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> geChoiceById(@PathVariable int id) {
        ChatChoices choice = chatChoicesService.getChoiceById(id);
        if (choice != null) {
            Map<String, Object> choiceDto = new HashMap<>();
            choiceDto.put("chat_id", choice.getChatID());
            choiceDto.put("choice", choice.getChoice());
            return new ResponseEntity<>(choiceDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllChoices() {
        List<ChatChoices> choices = chatChoicesService.getAllChoices();

        List<Map<String, Object>> choiceDtos = choices.stream()
                .map(choice -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("chat_id", choice.getChatID());
                    dto.put("choice", choice.getChoice());
                    return dto;
                })
                .toList();

        return new ResponseEntity<>(choiceDtos, HttpStatus.OK);
    }
}