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
    private BankService bankService; // This will now correctly instantiate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_inquiry);

        textViewDisplayBalance = findViewById(R.id.textViewDisplayBalance);
        buttonBackToMainFromBalance = findViewById(R.id.buttonBackToMainFromBalance);
        bankService = new BankService(); // Corrected by making BankService() public

        displayBalance();

        buttonBackToMainFromBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous activity (MainActivity)
            }
        });
    }

    private void displayBalance() {
        // Corrected: SessionManager.getCurrentAccount() is now correctly static
        Account currentAccount = SessionManager.getCurrentAccount();
        if (currentAccount != null) {
            // Corrected: getBalance(Account) method is now correctly found in BankService
            double balance = bankService.getBalance(currentAccount);
            if (balance != -1.0) { // Check for valid balance
                textViewDisplayBalance.setText(String.format(Locale.getDefault(), "$%.2f", balance));
            } else {
                Toast.makeText(this, "Could not retrieve balance. Please try again.", Toast.LENGTH_SHORT).show();
                textViewDisplayBalance.setText("$Error");
            }
        } else {
            Toast.makeText(this, "Account not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish(); // Go back to login or main if session is invalid
        }
    }
}