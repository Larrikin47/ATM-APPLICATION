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
        User user1 = new User("ABDIKADIR BONAYA", "1234", "ACC001");
        User user2 = new User("ANNRITA WAMBUI", "1234", "ACC002");
        User user3 = new User("MOHAMED MUKHTAR", "1234", "ACC003");
        User user4 = new User("ANNAH MAKIO", "1234", "ACC004");
        User user5 = new User("ABDISAMAD ABDI", "1234", "ACC005");
        User user6 = new User("NGETICH ENOCK", "1234", "ACC006");
        User user7 = new User("CHRISTINE ADHIAMBO", "1234", "ACC007");
        User user8 = new User("testUser", "1234", "ACC008");

        users.put(user1.getUsername(), user1);
        users.put(user2.getUsername(), user2);
        users.put(user3.getUsername(), user3);
        users.put(user4.getUsername(), user4);
        users.put(user5.getUsername(), user5);
        users.put(user6.getUsername(), user6);
        users.put(user7.getUsername(), user7);
        users.put(user8.getUsername(), user8);

        accounts.put("ACC001", new Account("ACC001", 14000.00));
        accounts.put("ACC002", new Account("ACC002", 1100.00));
        accounts.put("ACC003", new Account("ACC003", 8000.00));
        accounts.put("ACC004", new Account("ACC004", 6500.00));
        accounts.put("ACC005", new Account("ACC005", 7600.00));
        accounts.put("ACC006", new Account("ACC006", 12000.00));
        accounts.put("ACC007", new Account("ACC007", 52800.00));
        accounts.put("ACC008", new Account("ACC008", 1000.00));


    }

    public BankService() {
        // No specific initialization needed here for this simple in-memory version
    }

    public User authenticateUser(String username, String pin) {
        User user = users.get(username);
        if (user != null && user.getPin().equals(pin)) {
            return user;
        }
        return null;
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public boolean deposit(Account account, double amount) {
        if (account != null && amount > 0) {
            account.deposit(amount);
            addTransaction(account.getAccountNumber(), "Deposit", amount, account.getBalance());
            return true;
        }
        return false;
    }

    public boolean withdraw(Account account, double amount) {
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
        return -1.0;
    }

    private void addTransaction(String accountNumber, String type, double amount, double newBalance) {
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