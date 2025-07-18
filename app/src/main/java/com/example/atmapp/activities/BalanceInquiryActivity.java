package com.example.atmapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.atmapp.R;
import com.example.atmapp.models.Account;
import com.example.atmapp.services.BankService;
import com.example.atmapp.utils.SessionManager;

import java.util.Locale;

public class BalanceInquiryActivity extends AppCompatActivity {

    private TextView textViewDisplayBalance;
    private Button buttonBackToMainFromBalance;
    private BankService bankService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_inquiry);

        textViewDisplayBalance = findViewById(R.id.textViewDisplayBalance);
        buttonBackToMainFromBalance = findViewById(R.id.buttonBackToMainFromBalance);
        bankService = new BankService();

        displayBalance();

        buttonBackToMainFromBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void displayBalance() {
        Account currentAccount = SessionManager.getCurrentAccount();
        if (currentAccount != null) {
            double balance = bankService.getBalance(currentAccount);
            if (balance != -1.0) {
                textViewDisplayBalance.setText(String.format(Locale.getDefault(), "KSH:%.2f", balance));
            } else {
                Toast.makeText(this, "Could not retrieve balance. Please try again.", Toast.LENGTH_SHORT).show();
                textViewDisplayBalance.setText("$Error");
            }
        } else {
            Toast.makeText(this, "Account not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}