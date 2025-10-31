package com.example.dears.data.request;

// For the /register and /save endpoints

import java.time.LocalDate;

public class changeUserRequest {
    private String username;
    private String password;
    private LocalDate date;
    private String avatar;

    public changeUserRequest(String u, String p, LocalDate d, String a) {
        username = u;
        password = p;
        date = d;
        avatar = a;
    }
}
