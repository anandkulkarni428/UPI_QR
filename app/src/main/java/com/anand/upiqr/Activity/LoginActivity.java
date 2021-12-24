package com.anand.upiqr.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.biometrics.BiometricManager;

import androidx.biometric.BiometricPrompt;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anand.upiqr.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
    TextView regTxt, msgtex;
    EditText userIdTxt, passwordTxt;
    Button userLogBtn;
    SharedPreferences sP;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String UserId = "phoneKey";
    public static final String Password = "passwordKey";
    public static String getUserId = "";
    public static String getPassword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        userIdTxt = findViewById(R.id.user_id_text);
        passwordTxt = findViewById(R.id.password_text);
        regTxt = findViewById(R.id.reg_text);
        msgtex = findViewById(R.id.msgtext);
        userLogBtn = findViewById(R.id.login_btn);

        sP = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        getUserId = sP.getString(UserId, "");
        getPassword = sP.getString(Password, "");

        Log.d("TAG", getUserId + getPassword);

        if (getUserId != null)
            userIdTxt.setText(getUserId);

        bioProp();

        androidx.biometric.BiometricManager biometricManager;
        biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {

            // this means we can use biometric sensor
            case BiometricManager.BIOMETRIC_SUCCESS:
                msgtex.setText("You can use the fingerprint sensor to login");
                msgtex.setTextColor(Color.parseColor("#fafafa"));
                break;

            // this means that the device doesn't have fingerprint sensor
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:

                break;

            // this means that biometric sensor is not available
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                msgtex.setText("The biometric sensor is currently unavailable");
//                loginbutton.setVisibility(View.GONE);
                break;

            // this means that the device doesn't contain your fingerprint
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                msgtex.setText("Your device doesn't have fingerprint saved,please check your security settings");
//                loginbutton.setVisibility(View.GONE);
                break;
        }

        userLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.requireNonNull(userIdTxt.getText()).toString().isEmpty() || !userIdTxt.getText().toString().equals(getUserId)) {
                    Toast.makeText(LoginActivity.this, "Please enter correct user id!", Toast.LENGTH_SHORT).show();
                } else if (Objects.requireNonNull(passwordTxt.getText()).toString().isEmpty() || !passwordTxt.getText().toString().equals(getPassword)) {
                    Toast.makeText(LoginActivity.this, "Please enter correct password!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(intent);
                }
            }
        });
        regTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void bioProp() {
        // creating a variable for our Executor
        Executor executor = ContextCompat.getMainExecutor(this);
        // this will give us result of AUTHENTICATION
        final BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        // creating a variable for our promptInfo
        // BIOMETRIC DIALOG
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("UPI QR").setSubtitle("Easy things")
                .setDescription("Use your fingerprint to login ").setNegativeButtonText("Cancel").build();
        biometricPrompt.authenticate(promptInfo);
    }
}