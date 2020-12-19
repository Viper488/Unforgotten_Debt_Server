package com.company.domain;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public class LogginData {
    private String nick;
    private String password;

    public String getNick() {
        return nick;
    }

    public String getPassword() {
        return password;
    }
}
