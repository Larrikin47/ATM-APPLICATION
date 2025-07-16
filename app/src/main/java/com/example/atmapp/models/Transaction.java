package com.example.atmapp.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    private String accountNumber;
    private TransactionType type; // Using enum for transaction type
    private double amount;
    private double newBalance; // Added to store balance after transaction
    private long timestamp;

    // Enum for clear transaction types
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, BALANCE_INQUIRY
    }

    // Corrected constructor to match parameters from BankService.addTransaction
    public Transaction(String accountNumber, String typeString, double amount, double newBalance) {
        this.accountNumber = accountNumber;
        // Convert string type to enum
        try {
            this.type = TransactionType.valueOf(typeString.toUpperCase(Locale.ROOT).replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            // Handle cases where typeString doesn't match enum names
            this.type = null; // Or throw an error, log, etc.
        }
        this.amount = amount;
        this.newBalance = newBalance;
        this.timestamp = System.currentTimeMillis();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() { // Returns enum type
        return type;
    }

    public double getAmount() {
        return amount;
    }

    // Added getNewBalance()
    public double getNewBalance() {
        return newBalance;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // Helper to get type as String for display, if needed
    public String getTypeString() {
        return type != null ? type.name().replace("_", " ") : "UNKNOWN";
    }
}