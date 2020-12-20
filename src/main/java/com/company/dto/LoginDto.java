package com.company.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LoginDto {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
