package com.example.atmapp.activities;

import android.app.AlertDialog;
import android.content.Intent; // Needed for sharing
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryActivity extends AppCompatActivity {

    private ListView listViewTransactions;
    private Button buttonBackToMainFromHistory;
    private Button buttonGetReceipt; // Declare the new button
    private BankService bankService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        listViewTransactions = findViewById(R.id.listViewTransactions);
        buttonBackToMainFromHistory = findViewById(R.id.buttonBackToMainFromHistory);
        buttonGetReceipt = findViewById(R.id.buttonGetReceipt); // Initialize the new button
        bankService = new BankService();

        displayTransactionHistory();

        buttonBackToMainFromHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set OnClickListener for the new receipt button
        buttonGetReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateAndShowReceipt();
            }
        });
    }

    private void displayTransactionHistory() {
        Account currentAccount = SessionManager.getCurrentAccount();
        if (currentAccount != null) {
            // Get transactions from BankService (or SessionManager if stored there)
            List<Transaction> transactions = bankService.getTransactionHistory(currentAccount.getAccountNumber());

            if (transactions.isEmpty()) {
                Toast.makeText(this, "No transactions found for this account.", Toast.LENGTH_SHORT).show();
            }

            // Using android.R.layout.simple_list_item_1 for basic display
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1);

            for (Transaction t : transactions) {
                // Adapt the display format if needed, ensuring currency is KSH
                adapter.add(
                        String.format(Locale.getDefault(), "Type: %s\n", t.getTypeString()) +
                                (t.getType() == Transaction.TransactionType.DEPOSIT || t.getType() == Transaction.TransactionType.WITHDRAWAL ?
                                        String.format(Locale.getDefault(), "Amount: KSH %.2f\n", t.getAmount()) : "") + // Changed to KSH
                                String.format(Locale.getDefault(), "New Balance: KSH %.2f\n", t.getNewBalance()) + // Changed to KSH
                                String.format(Locale.getDefault(), "Date: %s", t.getFormattedTimestamp())
                );
            }

            listViewTransactions.setAdapter(adapter);

        } else {
            Toast.makeText(this, "Account not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // --- Receipt Generation Methods ---

    private void generateAndShowReceipt() {
        Account currentAccount = SessionManager.getCurrentAccount();
        // Get transactions directly from BankService for the receipt, as SessionManager might not hold all if not set up to do so
        List<Transaction> transactions = null;
        if (currentAccount != null) {
            transactions = bankService.getTransactionHistory(currentAccount.getAccountNumber());
        }

        if (currentAccount == null || transactions == null || transactions.isEmpty()) {
            Toast.makeText(this, "No transactions or account data to generate receipt.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder receiptBuilder = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        receiptBuilder.append("--- ATM Transaction Receipt ---\n");
        receiptBuilder.append("Date & Time: ").append(currentDateTime).append("\n");
        receiptBuilder.append("Account Number: ").append(currentAccount.getAccountNumber()).append("\n");
        receiptBuilder.append("------------------------------\n");
        receiptBuilder.append("Transactions:\n");

        // Iterate through all transactions and add them to the receipt
        for (Transaction transaction : transactions) {
            receiptBuilder.append("  Type: ").append(transaction.getTypeString()).append("\n");
            // Only show amount if it's a deposit or withdrawal
            if (transaction.getType() == Transaction.TransactionType.DEPOSIT || transaction.getType() == Transaction.TransactionType.WITHDRAWAL) {
                receiptBuilder.append("  Amount: KSH ").append(String.format(Locale.getDefault(), "%.2f", transaction.getAmount())).append("\n");
            }
            receiptBuilder.append("  New Balance: KSH ").append(String.format(Locale.getDefault(), "%.2f", transaction.getNewBalance())).append("\n");
            receiptBuilder.append("  Timestamp: ").append(transaction.getFormattedTimestamp()).append("\n"); // Using already formatted timestamp
            receiptBuilder.append("  ---\n"); // Separator for each transaction
        }

        receiptBuilder.append("------------------------------\n");
        receiptBuilder.append("Current Balance: KSH ").append(String.format(Locale.getDefault(), "%.2f", currentAccount.getBalance())).append("\n");
        receiptBuilder.append("Thank you for using our ATM!\n");
        receiptBuilder.append("------------------------------\n");

        showReceiptDialog(receiptBuilder.toString());
    }

    private void showReceiptDialog(String receiptContent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Transaction Receipt");

        TextView receiptTextView = new TextView(this);
        receiptTextView.setText(receiptContent);
        receiptTextView.setPadding(30, 30, 30, 30);
        receiptTextView.setTextIsSelectable(true); // Allow copying text

        builder.setView(receiptTextView);

        builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        // Optionally, add a "Share" button
        builder.setNegativeButton("Share", (dialog, id) -> {
            shareReceipt(receiptContent);
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void shareReceipt(String receiptContent) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, receiptContent);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "ATM Transaction Receipt");
        startActivity(Intent.createChooser(shareIntent, "Share Receipt Via"));
    }
}