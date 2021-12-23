package com.anand.upiqr.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.anand.upiqr.R;
import com.anand.upiqr.databinding.ActivityMainBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextInputEditText amountTxt, upiIdTxt, firstNameTxt, lastNameTxt;
    Button qrBtn;
    ActivityMainBinding binding;

    SharedPreferences sP;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String FirstName = "firstNameKey";
    public static final String LastName = "lastNameKey";
    public static final String UPI_Id = "UPI_Key";
    public static String getFirstName = "";
    public static String getLastName = "";
    public static String getUPI_Id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        qrBtn = findViewById(R.id.qr_btn);

        amountTxt = findViewById(R.id.amount_text);
        upiIdTxt = findViewById(R.id.upi_id_text);
        firstNameTxt = findViewById(R.id.first_name_text);
        lastNameTxt = findViewById(R.id.last_name_text);

        sP = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        getFirstName = sP.getString(FirstName, "");
        getLastName = sP.getString(LastName, "");
        getUPI_Id = sP.getString(UPI_Id, "");

        firstNameTxt.setText(getFirstName);
        lastNameTxt.setText(getLastName);
        upiIdTxt.setText(getUPI_Id);
        upiIdTxt.setClickable(false);
        upiIdTxt.setEnabled(false);


        binding.amountChip.setOnCheckedChangeListener((group, i) -> {
            Chip chip = binding.amountChip.findViewById(i);
            if (chip != null)
                if (chip.isChecked()) {
                    amountTxt.setText(chip.getText());
                    Toast.makeText(getApplicationContext(), "Amount is " + chip.getText(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Amount is 0 " , Toast.LENGTH_SHORT).show();
                }
        });


        qrBtn.setOnClickListener(v -> {
            if (!Objects.requireNonNull(amountTxt.getText()).toString().isEmpty()) {
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
        intent.putExtra("LAST_NAME", Objects.requireNonNull(lastNameTxt.getText()). toString());
        intent.putExtra("TRANS_ID", generateTransId());
        startActivity(intent);
    }

    public String generateTransId(){

        String trans_id = "UPIQR"+System.currentTimeMillis();

        return trans_id;
    }
}