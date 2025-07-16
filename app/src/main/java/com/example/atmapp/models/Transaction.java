package com.example.atmapp.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    private String accountNumber;
    private TransactionType type;
    private double amount;
    private double newBalance;
    private long timestamp;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, BALANCE_INQUIRY
    }

    public Transaction(String accountNumber, String typeString, double amount, double newBalance) {
        this.accountNumber = accountNumber;
        try {
            this.type = TransactionType.valueOf(typeString.toUpperCase(Locale.ROOT).replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            this.type = null; // Or handle more robustly
        }
        this.amount = amount;
        this.newBalance = newBalance;
        this.timestamp = System.currentTimeMillis();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

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

    public String getTypeString() {
        return type != null ? type.name().replace("_", " ") : "UNKNOWN";
    }
}