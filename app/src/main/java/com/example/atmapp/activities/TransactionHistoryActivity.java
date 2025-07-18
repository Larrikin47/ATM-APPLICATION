package com.example.atmapp.activities;

import android.Manifest; // For permissions
import android.app.AlertDialog;
import android.content.ContentValues; // For MediaStore
import android.content.Intent;
import android.content.pm.PackageManager; // For permissions
import android.net.Uri; // For MediaStore
import android.os.Build; // For Build.VERSION
import android.os.Bundle;
import android.os.Environment; // For Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore; // For MediaStore
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull; // For @NonNull annotation
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat; // For requesting permissions
import androidx.core.content.ContextCompat; // For checking permissions

import com.example.atmapp.R;
import com.example.atmapp.models.Account;
import com.example.atmapp.models.Transaction;
import com.example.atmapp.services.BankService;
import com.example.atmapp.utils.SessionManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException; // For I/O exceptions
import java.io.OutputStream; // For MediaStore
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryActivity extends AppCompatActivity {

    private ListView listViewTransactions;
    private Button buttonBackToMainFromHistory;
    private Button buttonGetReceipt; // The "Get Receipt" button from your XML
    private BankService bankService;

    // Unique request code for storage permission
    private static final int PERMISSION_REQUEST_CODE = 101;

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

        // Set OnClickListener for the "Get Receipt" button
        buttonGetReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When "Get Receipt" is clicked, decide whether to ask for permission or directly save
                handleReceiptDownload();
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
                                        String.format(Locale.getDefault(), "Amount: KSH %.2f\n", t.getAmount()) : "") +
                                String.format(Locale.getDefault(), "New Balance: KSH %.2f\n", t.getNewBalance()) +
                                String.format(Locale.getDefault(), "Date: %s", t.getFormattedTimestamp())
                );
            }

            listViewTransactions.setAdapter(adapter);

        } else {
            Toast.makeText(this, "Account not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // --- Receipt Generation and Download Logic ---

    private void handleReceiptDownload() {
        String receiptContent = generateReceiptContent();
        if (receiptContent == null) {
            Toast.makeText(this, "Failed to generate receipt content.", Toast.LENGTH_SHORT).show();
            return;
        }

        // For Android 10 (API 29) and above, use MediaStore API. No explicit permission needed for Downloads.
        // For Android 9 (API 28) and below, check and request WRITE_EXTERNAL_STORAGE permission.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveReceiptToDownloadsUsingMediaStore(receiptContent);
        } else {
            // For older Android versions, check for WRITE_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                // Permission is already granted, proceed to save
                saveReceiptToLegacyExternalStorage(receiptContent);
            }
        }
    }

    // Callback for runtime permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, now save the receipt
                saveReceiptToLegacyExternalStorage(generateReceiptContent());
            } else {
                // Permission denied
                Toast.makeText(this, "Storage permission denied. Cannot save receipt.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Generates the formatted text content for the receipt.
     * @return The receipt content string, or null if no data.
     */
    private String generateReceiptContent() {
        Account currentAccount = SessionManager.getCurrentAccount();
        List<Transaction> transactions = null;
        if (currentAccount != null) {
            transactions = bankService.getTransactionHistory(currentAccount.getAccountNumber());
        }

        if (currentAccount == null || transactions == null || transactions.isEmpty()) {
            return null; // Indicate no content
        }

        StringBuilder receiptBuilder = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        receiptBuilder.append("--- ATM Transaction Receipt ---\n");
        receiptBuilder.append("Date & Time: ").append(currentDateTime).append("\n");
        receiptBuilder.append("Account Number: ").append(currentAccount.getAccountNumber()).append("\n");
        receiptBuilder.append("------------------------------\n");
        receiptBuilder.append("Transactions:\n");

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

        return receiptBuilder.toString();
    }

    /**
     * Saves the receipt content to the Downloads directory using MediaStore (Android 10+).
     */
    private void saveReceiptToDownloadsUsingMediaStore(String receiptContent) {
        String fileName = "ATM_Receipt_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".txt";

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        // Save to the public Downloads directory
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = null;
        OutputStream os = null;
        try {
            uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
            if (uri != null) {
                os = getContentResolver().openOutputStream(uri);
                if (os != null) {
                    os.write(receiptContent.getBytes());
                    Toast.makeText(this, "Receipt saved to Downloads: " + fileName, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Failed to open output stream.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to create new MediaStore entry.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving receipt: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Saves the receipt content to the Downloads directory using direct file access (Android 9 and below).
     */
    private void saveReceiptToLegacyExternalStorage(String receiptContent) {
        String fileName = "ATM_Receipt_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".txt";

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadsDir.exists()) {
            if (!downloadsDir.mkdirs()) { // Create the directory if it doesn't exist
                Toast.makeText(this, "Failed to create Downloads directory.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        File file = new File(downloadsDir, fileName);

        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.append(receiptContent);
            writer.flush();
            Toast.makeText(this, "Receipt saved to Downloads: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving receipt: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // You can remove or adapt the showReceiptDialog and shareReceipt if you only want download
    // For now, I've kept them if you wanted to offer choice. If only download, remove.
    private void showReceiptDialog(String receiptContent) {
        // This method is now implicitly called via handleReceiptDownload logic.
        // You might consider if you still want a dialog showing the content before saving,
        // or just directly save and show a Toast.
        // For simplicity, the current 'handleReceiptDownload' directly saves.
        // If you want a dialog *with* a save button, you'd integrate the save logic there.
    }
}