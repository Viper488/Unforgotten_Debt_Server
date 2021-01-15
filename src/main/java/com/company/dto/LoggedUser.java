package com.company.dto;

public class LoggedUser {
    private Integer user_id;
    private String nick;

    public LoggedUser() {
    }

    public LoggedUser(Integer user_id, String nick) {
        this.user_id = user_id;
        this.nick = nick;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
