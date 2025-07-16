package com.example.atmapp.utils;

import com.example.atmapp.models.Account;
import com.example.atmapp.models.User;

public class SessionManager {
    private static User currentUser;
    private static Account currentAccount;

    // All methods are static, no getInstance() needed
    public static void login(User user, Account account) {
        currentUser = user;
        currentAccount = account;
    }

    public static void logout() {
        currentUser = null;
        currentAccount = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static Account getCurrentAccount() {
        return currentAccount;
    }

    public static boolean isLoggedIn() {
        return currentUser != null && currentAccount != null;
    }
}