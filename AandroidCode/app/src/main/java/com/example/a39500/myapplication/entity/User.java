package com.example.a39500.myapplication.entity;

public class User {
    String username;
    String password;

    public User(String usm, String pwd){
        this.username = usm;
        this.password = pwd;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
