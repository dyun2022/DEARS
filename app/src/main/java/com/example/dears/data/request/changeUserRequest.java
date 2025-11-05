package com.example.dears.data.request;


import java.time.LocalDate;

public class changeUserRequest {
    private Integer userID;
    private String  username;
    private String  password;
    private LocalDate birthday;
    private String  avatar;

    public changeUserRequest() {}

    public changeUserRequest(String u, String p, LocalDate b, String a) {
        this.username = u;
        this.password = p;
        this.birthday = b;
        this.avatar   = a;
    }

    public changeUserRequest(Integer id, String u, String p, LocalDate b, String a) {
        this.userID   = id;
        this.username = u;
        this.password = p;
        this.birthday = b;
        this.avatar   = a;
    }

    public Integer getUserID() { return userID; }
    public void setUserID(Integer userID) { this.userID = userID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
