package com.anand.upiqr.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anand.upiqr.R;
import com.anand.upiqr.Utils.Transaction;
import com.anand.upiqr.Utils.TransactionDataBaseHandler;
import com.anand.upiqr.databinding.ActivityMainBinding;
import com.google.android.material.chip.Chip;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText amountTxt, upiIdTxt, firstNameTxt, lastNameTxt;
    Button qrBtn;
    ActivityMainBinding binding;
    AnimatedVectorDrawable animatedVectorDrawable;

    SharedPreferences sP;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String FirstName = "firstNameKey";
    public static final String LastName = "lastNameKey";
    public static final String UPI_Id = "UPI_Key";
    public static final String UserId = "phoneKey";
    public static String getUserId = "";
    public static String getFirstName = "";
    public static String getLastName = "";
    public static String getUPI_Id = "";
    public static String getAmount = "";
    TransactionDataBaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new TransactionDataBaseHandler(this);

        qrBtn = findViewById(R.id.qr_btn);

        amountTxt = findViewById(R.id.amount_text);
        upiIdTxt = findViewById(R.id.upi_id_text);
        firstNameTxt = findViewById(R.id.first_name_text);
        lastNameTxt = findViewById(R.id.last_name_text);

        sP = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        getFirstName = sP.getString(FirstName, "");
        getLastName = sP.getString(LastName, "");
        getUPI_Id = sP.getString(UPI_Id, "");
        getUserId = sP.getString(UserId, "");

        firstNameTxt.setText(getFirstName);
        lastNameTxt.setText(getLastName);
        upiIdTxt.setText(getUPI_Id);
        upiIdTxt.setClickable(false);
        upiIdTxt.setEnabled(false);
        binding.userIdText.setText(getUserId);

        runAnimation();


        binding.amountChip.setOnCheckedChangeListener((group, i) -> {
            Chip chip = binding.amountChip.findViewById(i);
            if (chip != null)
                if (chip.isChecked()) {
                    amountTxt.setText(chip.getText());
                    Toast.makeText(getApplicationContext(), "Amount is " + chip.getText(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Amount is 0 ", Toast.LENGTH_SHORT).show();
                }
        });

        binding.historyBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TransactionHistoryActivity.class);
            startActivity(intent);
        });


        qrBtn.setOnClickListener(v -> {
            if (!Objects.requireNonNull(amountTxt.getText()).toString().isEmpty()) {
                getAmount = amountTxt.getText().toString();
                sendData();
            } else {
                Toast.makeText(MainActivity.this, "Please enter the amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendData() {
        Intent intent = new Intent(MainActivity.this, QR_Activity.class);
        intent.putExtra("AMOUNT", Objects.requireNonNull(amountTxt.getText()).toString());
        intent.putExtra("UPI_ID", Objects.requireNonNull(upiIdTxt.getText()).toString());
        intent.putExtra("FIRST_NAME", Objects.requireNonNull(firstNameTxt.getText()).toString());
        intent.putExtra("LAST_NAME", Objects.requireNonNull(lastNameTxt.getText()).toString());
        intent.putExtra("TRANS_ID", generateTransId());
        saveToDB();
        startActivity(intent);
    }

    public String generateTransId() {

        return getString(R.string.upi_qr) + System.currentTimeMillis();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void runAnimation() {
        animatedVectorDrawable =
                (AnimatedVectorDrawable) getDrawable(R.drawable.avd_anim_upi);
        binding.titleLogo.setImageDrawable(animatedVectorDrawable);
        animatedVectorDrawable.start();
    }

    public void saveToDB() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateTransId());
        transaction.setFirstName(firstNameTxt.getText().toString());
        transaction.setLastName(lastNameTxt.getText().toString());
        transaction.setUpiId(upiIdTxt.getText().toString());
        transaction.setAmount(getAmount);
        Log.d("TAG", transaction.getTransactionId() + transaction.getFirstName() + transaction.getLastName() + transaction.getUpiId() + transaction.getAmount());
        db.addTransaction(transaction);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animatedVectorDrawable.stop();
    }
}