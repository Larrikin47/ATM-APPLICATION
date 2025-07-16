package com.example.atmapp.services;

import com.example.atmapp.models.Account;
import com.example.atmapp.models.Transaction;
import com.example.atmapp.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankService {

    private static final Map<String, User> users = new HashMap<>();
    private static final Map<String, Account> accounts = new HashMap<>();
    private static final Map<String, List<Transaction>> transactionHistory = new HashMap<>();

    static {
        // Initialize some dummy data
        User user1 = new User("user123", "1234", "ACC001");
        User user2 = new User("testuser", "5678", "ACC002");

        users.put(user1.getUsername(), user1);
        users.put(user2.getUsername(), user2);

        // Corrected: Account constructor call (String, double)
        accounts.put("ACC001", new Account("ACC001", 1000.00));
        accounts.put("ACC002", new Account("ACC002", 500.00));
    }

    public BankService() {
        // No specific initialization needed here for this simple in-memory version
    }

    public User authenticateUser(String username, String pin) {
        User user = users.get(username);
        if (user != null && user.getPin().equals(pin)) {
            return user;
        }
        return null; // Authentication failed
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public boolean deposit(Account account, double amount) {
        if (account != null && amount > 0) {
            // Corrected: account.deposit() method is now available
            account.deposit(amount);
            addTransaction(account.getAccountNumber(), "Deposit", amount, account.getBalance());
            return true;
        }
        return false;
    }

    public boolean withdraw(Account account, double amount) {
        // Corrected: account.withdraw() method is now available
        if (account != null && account.withdraw(amount)) {
            addTransaction(account.getAccountNumber(), "Withdrawal", amount, account.getBalance());
            return true;
        }
        return false;
    }

    public double getBalance(Account account) {
        if (account != null) {
            addTransaction(account.getAccountNumber(), "Balance Inquiry", 0.0, account.getBalance());
            return account.getBalance();
        }
        return -1.0; // Indicate error
    }

    private void addTransaction(String accountNumber, String type, double amount, double newBalance) {
        // Corrected: Transaction constructor call now matches the updated Transaction class
        Transaction transaction = new Transaction(accountNumber, type, amount, newBalance);
        if (!transactionHistory.containsKey(accountNumber)) {
            transactionHistory.put(accountNumber, new ArrayList<>());
        }
        transactionHistory.get(accountNumber).add(transaction);
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionHistory.getOrDefault(accountNumber, new ArrayList<>());
    }
}