package com.example.dears.data.request;

// For the /register and /save endpoints

import java.time.LocalDate;

public class changeUserRequest {
    private String username;
    private String password;
    private LocalDate birthday;
    private String avatar;

    public changeUserRequest(String u, String p, LocalDate b, String a) {
        username = u;
        password = p;
        birthday = b;
        avatar = a;
    }
}
