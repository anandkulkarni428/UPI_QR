package com.anand.upiqr.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anand.upiqr.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    TextView regTxt;
    TextInputEditText userIdTxt, passwordTxt;
    Button userLogBtn;
    SharedPreferences sP;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String UserId = "phoneKey";
    public static final String Password = "passwordKey";
    public static String getUserId = "" ;
    public static String getPassword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        userIdTxt = findViewById(R.id.user_id_text);
        passwordTxt = findViewById(R.id.password_text);
        regTxt = findViewById(R.id.reg_text);
        userLogBtn = findViewById(R.id.login_btn);

        sP = getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);
        getUserId = sP.getString(UserId,"");
        getPassword = sP.getString(Password,"");

        Log.d("TAG",getUserId+getPassword);

        userLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.requireNonNull(userIdTxt.getText()).toString().isEmpty() || !userIdTxt.getText().toString().equals(getUserId)){
                    Toast.makeText(LoginActivity.this, "Please enter correct user id!", Toast.LENGTH_SHORT).show();
                } else if (Objects.requireNonNull(passwordTxt.getText()).toString().isEmpty() || !passwordTxt.getText().toString().equals(getPassword)){
                    Toast.makeText(LoginActivity.this, "Please enter correct password!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(intent);
                }
            }
        });
        regTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}