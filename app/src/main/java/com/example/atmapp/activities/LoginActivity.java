package com.example.atmapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.atmapp.R;
import com.example.atmapp.models.Account;
import com.example.atmapp.models.User;
import com.example.atmapp.services.BankService;
import com.example.atmapp.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPin;
    private Button buttonLogin;

    private BankService bankService;
    // SessionManager is now purely static, no need for an instance field
    // private SessionManager sessionManager; // Remove this line if it exists

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPin = findViewById(R.id.editTextPin);
        buttonLogin = findViewById(R.id.buttonLogin);

        bankService = new BankService(); // Corrected: Direct instantiation
        // sessionManager = SessionManager.getInstance(); // Removed: SessionManager is static

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = editTextUsername.getText().toString().trim();
        String pin = editTextPin.getText().toString().trim();

        if (username.isEmpty() || pin.isEmpty()) {
            Toast.makeText(this, "Please enter username and PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = bankService.authenticateUser(username, pin);

        if (user != null) {
            // Corrected: Use user.getUsername()
            Account account = bankService.getAccount(user.getAccountNumber()); // Assuming User stores account number

            if (account != null) {
                // Corrected: Use SessionManager.login()
                SessionManager.login(user, account);
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close LoginActivity so user can't go back with back button
            } else {
                Toast.makeText(this, "Account not found for this user.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid Username or PIN", Toast.LENGTH_SHORT).show();
        }
    }
}