package com.app.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ChatChoices")
public class ChatChoices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private int energy_id;

    @JoinColumn(name = "choice", nullable = false)
    private String choice;

    // empty constructor for JPA
    public ChatChoices() {}

    public ChatChoices(String choice) {
        this.choice = choice;
    }

    // Getters and Setters
    public int getChatID() {
        return energy_id;
    }
    public void setChatID(int energy_id) {
        this.energy_id = energy_id;
    }
    public String getChoice() {
        return choice;
    }
    public void setChoice(String choice) {
        this.choice = choice;
    }
}