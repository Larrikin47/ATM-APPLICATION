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

public class DepositActivity extends AppCompatActivity {

    private EditText editTextDepositAmount;
    private Button buttonConfirmDeposit;
    private TextView textViewCurrentBalanceDeposit;
    private BankService bankService; // This will now correctly instantiate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        editTextDepositAmount = findViewById(R.id.editTextDepositAmount);
        buttonConfirmDeposit = findViewById(R.id.buttonConfirmDeposit);
        textViewCurrentBalanceDeposit = findViewById(R.id.textViewCurrentBalanceDeposit);
        bankService = new BankService(); // Corrected by making BankService() public

        updateBalanceDisplay();

        buttonConfirmDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performDeposit();
            }
        });
    }

    private void updateBalanceDisplay() {
        Account currentAccount = SessionManager.getCurrentAccount();
        if (currentAccount != null) {
            textViewCurrentBalanceDeposit.setText(String.format(Locale.getDefault(), "Current Balance: $%.2f", currentAccount.getBalance()));
        }
    }

    private void performDeposit() {
        String amountString = editTextDepositAmount.getText().toString().trim();
        if (amountString.isEmpty()) {
            Toast.makeText(this, "Please enter an amount to deposit.", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountString);
        Account currentAccount = SessionManager.getCurrentAccount();

        if (currentAccount != null) {
            if (amount <= 0) {
                Toast.makeText(this, "Deposit amount must be positive.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (bankService.deposit(currentAccount, amount)) {
                Toast.makeText(this, String.format(Locale.getDefault(), "Successfully deposited $%.2f", amount), Toast.LENGTH_LONG).show();
                updateBalanceDisplay(); // Update balance display after successful deposit
                editTextDepositAmount.setText(""); // Clear the input field
            } else {
                Toast.makeText(this, "Deposit failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Account not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish(); // Go back to previous activity or login
        }
    }
}