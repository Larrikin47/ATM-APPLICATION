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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!SessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

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
            welcomeTextView.setText("Welcome!");
        }

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
                SessionManager.logout();
                Toast.makeText(MainActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}