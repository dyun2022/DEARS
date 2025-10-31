package com.example.dears.data.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

/**
 * User model. Should match the backend's User.
 */
public class User {
    @SerializedName("user_id")
    private int user_id;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("birthday")
    private LocalDate birthday;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("pet")
    private Pet pet;

    public User(int user_id, String username, String password, LocalDate birthday, String avatar) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        this.avatar = avatar;
    }
    // Getters and Setters
    public int getUserID() {
        return user_id;
    }
    public void setUserID(int user_id) {
        this.user_id = user_id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public LocalDate getBirthday() {
        return birthday;
    }
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }
}