package com.app.project.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

// making the database for Users
@Entity
@Table(name = "Users")
public class User{
    @Id // PRIAMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO-INCREMENT
    @Column(name = "user_id", nullable = false)
    private int user_id;

    @Column(name = "username", length = 25, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 25, nullable = false, unique = true)
    private String password;

    @Column(name = "birthday", length = 25, nullable = false)
    private Date birthday;

    @Column(name = "avatar", length = 25, nullable = false)
    private String avatar;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Pet pet = new Pet();

    // empty constructor for JPA
    public User() {}

    public User(int user_id, String username, String password, Date birthday, String avatar) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        this.avatar = avatar;
    }

    public boolean verify(String username, String password) {
        if (this.username.equals(username) && this.password.equals(password)) {
            return true;
        }
        return false;
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
    public Date getBirthday() {
        return birthday;
    }
    public void setBirthday(Date birthday) {
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
