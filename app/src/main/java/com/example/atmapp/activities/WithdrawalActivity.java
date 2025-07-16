package com.example.atmapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.atmapp.R;
import com.example.atmapp.models.Account;
import com.example.atmapp.services.BankService;
import com.example.atmapp.utils.SessionManager;

import java.util.Locale;

public class WithdrawalActivity extends AppCompatActivity {

    private EditText editTextWithdrawalAmount;
    private Button buttonConfirmWithdrawal;
    private TextView textViewCurrentBalanceWithdrawal;
    private Button buttonBackToMainFromWithdrawal; // Added
    private BankService bankService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        editTextWithdrawalAmount = findViewById(R.id.editTextWithdrawalAmount);
        buttonConfirmWithdrawal = findViewById(R.id.buttonConfirmWithdrawal);
        textViewCurrentBalanceWithdrawal = findViewById(R.id.textViewCurrentBalanceWithdrawal);
        buttonBackToMainFromWithdrawal = findViewById(R.id.buttonBackToMainFromWithdrawal); // Initialized
        bankService = new BankService();

        updateBalanceDisplay();

        buttonConfirmWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performWithdrawal();
            }
        });

        buttonBackToMainFromWithdrawal.setOnClickListener(new View.OnClickListener() { // Listener for new button
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateBalanceDisplay() {
        Account currentAccount = SessionManager.getCurrentAccount();
        if (currentAccount != null) {
            textViewCurrentBalanceWithdrawal.setText(String.format(Locale.getDefault(), "Current Balance: $%.2f", currentAccount.getBalance()));
        } else {
            textViewCurrentBalanceWithdrawal.setText("Current Balance: N/A");
        }
    }

    private void performWithdrawal() {
        String amountString = editTextWithdrawalAmount.getText().toString().trim();
        if (amountString.isEmpty()) {
            Toast.makeText(this, "Please enter an amount to withdraw.", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountString);
        Account currentAccount = SessionManager.getCurrentAccount();

        if (currentAccount != null) {
            if (amount <= 0) {
                Toast.makeText(this, "Withdrawal amount must be positive.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (amount > currentAccount.getBalance()) {
                Toast.makeText(this, "Insufficient funds.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (bankService.withdraw(currentAccount, amount)) {
                Toast.makeText(this, String.format(Locale.getDefault(), "Successfully withdrew $%.2f", amount), Toast.LENGTH_LONG).show();
                updateBalanceDisplay();
                editTextWithdrawalAmount.setText("");
            } else {
                Toast.makeText(this, "Withdrawal failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Account not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}