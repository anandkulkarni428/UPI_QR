package com.anand.upiqr.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anand.upiqr.Adapters.TransHistoryAdapter;
import com.anand.upiqr.Utils.Transaction;
import com.anand.upiqr.Utils.TransactionDataBaseHandler;
import com.anand.upiqr.databinding.ActivityTransactionHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {
    ActivityTransactionHistoryBinding binding;
    TransactionDataBaseHandler db;
    List<Transaction> transactions;
    public static TextView transCountTXT;
    int transCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        transCountTXT = binding.transCountTxt;
        db = new TransactionDataBaseHandler(this);
        transactions = new ArrayList<>();
        transactions = db.getAllTransactions();
        transCount = db.getTransactionCount();

        LinearLayoutManager layoutManager = new LinearLayoutManager(TransactionHistoryActivity.this, RecyclerView.VERTICAL, false);
        binding.transRec.setLayoutManager(layoutManager);
        TransHistoryAdapter transHistoryAdapter = new TransHistoryAdapter(this, transactions);
        binding.transCountTxt.setText(String.valueOf(transCount));

        if (transCount == 0) {
            binding.noTransTxt.setVisibility(View.VISIBLE);
            binding.transRec.setVisibility(View.GONE);
        } else {
            binding.transRec.setAdapter(transHistoryAdapter);
            binding.noTransTxt.setVisibility(View.GONE);
            binding.transRec.setVisibility(View.VISIBLE);
        }


    }
}