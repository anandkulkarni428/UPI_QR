package com.anand.upiqr.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anand.upiqr.R;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    EditText firstNameTxt, lastNametxt, userIdTxt, passwordTxt,upiIdTxt;
    TextView logTxt;
    Button userRegBtn;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String FirstName = "firstNameKey";
    public static final String LastName = "lastNameKey";
    public static final String UserId = "phoneKey";
    public static final String UPI_Id = "UPI_Key";
    public static final String Password = "passwordKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firstNameTxt = findViewById(R.id.first_name_text);
        lastNametxt = findViewById(R.id.last_name_text);
        userIdTxt = findViewById(R.id.user_id_text);
        upiIdTxt = findViewById(R.id.upi_id_text);
        passwordTxt = findViewById(R.id.password_text);
        logTxt = findViewById(R.id.log_text);
        userRegBtn = findViewById(R.id.register_btn);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        logTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        userRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref(firstNameTxt.getText().toString(), lastNametxt.getText().toString(), userIdTxt.getText().toString(), passwordTxt.getText().toString(),upiIdTxt.getText().toString());
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void sharedPref(String fName, String lName, String userId, String userPass,String UPIId) {
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(FirstName, fName);
        editor.putString(LastName, lName);
        editor.putString(UserId, userId);
        editor.putString(Password, userPass);
        editor.putString(UPI_Id, UPIId);
        editor.commit();
        Toast.makeText(RegisterActivity.this, "Registered Successfully!", Toast.LENGTH_LONG).show();
    }
}