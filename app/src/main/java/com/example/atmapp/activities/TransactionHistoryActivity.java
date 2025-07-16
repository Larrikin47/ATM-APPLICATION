package com.example.atmapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.atmapp.R;
import com.example.atmapp.models.Account;
import com.example.atmapp.models.Transaction;
import com.example.atmapp.services.BankService;
import com.example.atmapp.utils.SessionManager;

import java.util.List;
import java.util.Locale;

public class TransactionHistoryActivity extends AppCompatActivity {

    private ListView listViewTransactions;
    private Button buttonBackToMainFromHistory;
    private BankService bankService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        listViewTransactions = findViewById(R.id.listViewTransactions);
        buttonBackToMainFromHistory = findViewById(R.id.buttonBackToMainFromHistory);
        bankService = new BankService();

        displayTransactionHistory();

        buttonBackToMainFromHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void displayTransactionHistory() {
        Account currentAccount = SessionManager.getCurrentAccount();
        if (currentAccount != null) {
            List<Transaction> transactions = bankService.getTransactionHistory(currentAccount.getAccountNumber());

            if (transactions.isEmpty()) {
                Toast.makeText(this, "No transactions found for this account.", Toast.LENGTH_SHORT).show();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1);

            for (Transaction t : transactions) {
                adapter.add(
                        String.format(Locale.getDefault(), "Type: %s\n", t.getTypeString()) +
                                (t.getType() == Transaction.TransactionType.DEPOSIT || t.getType() == Transaction.TransactionType.WITHDRAWAL ?
                                        String.format(Locale.getDefault(), "Amount: $%.2f\n", t.getAmount()) : "") +
                                String.format(Locale.getDefault(), "New Balance: $%.2f\n", t.getNewBalance()) +
                                String.format(Locale.getDefault(), "Date: %s", t.getFormattedTimestamp())
                );
            }

            listViewTransactions.setAdapter(adapter);

        } else {
            Toast.makeText(this, "Account not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}