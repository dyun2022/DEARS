package com.example.dears.data.request;

import java.time.LocalDate;

public class loginUserRequest {
    private String username;
    private String password;

    public loginUserRequest(String u, String p) {
        username = u;
        password = p;
    }
}
