package com.example.atmapp.models;

public class User {
    private String username;
    private String pin;
    private String accountNumber;

    public User(String username, String pin, String accountNumber) {
        this.username = username;
        this.pin = pin;
        this.accountNumber = accountNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPin() {
        return pin;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}