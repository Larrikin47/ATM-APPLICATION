package com.example.atmapp.models;

public class User {
    private String username;
    private String pin; // In a real app, this would be securely hashed!
    private String accountNumber; // Link to the user's account

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

    // For simplicity, we'll keep setters limited, or not include them if not needed
}