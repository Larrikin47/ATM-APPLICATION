package com.example.atmapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.atmapp.R;
import com.example.atmapp.models.User;
import com.example.atmapp.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private Button buttonDeposit;
    private Button buttonWithdrawal;
    private Button buttonBalanceInquiry;
    private Button buttonTransactionHistory;
    private Button buttonLogout;
    private TextView welcomeTextView;

    // SessionManager is static, no need for an instance field
    // private SessionManager sessionManager; // Remove this line if it exists

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // sessionManager = SessionManager.getInstance(); // Removed: SessionManager is static

        // Corrected: Use SessionManager.isLoggedIn()
        if (!SessionManager.isLoggedIn()) {
            // If not logged in, redirect to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity
            return;
        }

        // Initialize UI elements
        welcomeTextView = findViewById(R.id.welcomeTextView);
        buttonDeposit = findViewById(R.id.buttonDeposit);
        buttonWithdrawal = findViewById(R.id.buttonWithdrawal);
        buttonBalanceInquiry = findViewById(R.id.buttonBalanceInquiry);
        buttonTransactionHistory = findViewById(R.id.buttonTransactionHistory);
        buttonLogout = findViewById(R.id.buttonLogout);

        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            welcomeTextView.setText("Welcome, " + currentUser.getUsername() + "!");
        } else {
            welcomeTextView.setText("Welcome!"); // Fallback
        }


        // Set up button listeners
        buttonDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DepositActivity.class));
            }
        });

        buttonWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WithdrawalActivity.class));
            }
        });

        buttonBalanceInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BalanceInquiryActivity.class));
            }
        });

        buttonTransactionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TransactionHistoryActivity.class));
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Corrected: Use SessionManager.logout()
                SessionManager.logout();
                Toast.makeText(MainActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close MainActivity
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-check session status in onResume in case user comes back from a child activity
        if (!SessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}