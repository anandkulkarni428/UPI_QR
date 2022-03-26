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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anand.upiqr.Interface.ApiInterface;
import com.anand.upiqr.R;
import com.anand.upiqr.Utils.AppPreferences;
import com.anand.upiqr.Utils.ConnectionChecking;
import com.anand.upiqr.Utils.HttpHelpers;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.Executor;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    TextView regTxt, msgtex;
    EditText userIdTxt, passwordTxt;
    Button userLogBtn;
    ImageView bioMatImg;

    ApiInterface apiInterface;
    ConnectionChecking checking;
    String passRegex = "[><?@+'`~^%&\\*\\[\\]\\{\\}.!#|\\\\\\\"$';,:;=/\\(\\),\\-\\w+]*";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        userIdTxt = findViewById(R.id.user_id_text);
        passwordTxt = findViewById(R.id.password_text);
        regTxt = findViewById(R.id.reg_text);
        userLogBtn = findViewById(R.id.login_btn);
        bioMatImg = findViewById(R.id.biomat_img);

        checking = new ConnectionChecking();

//        if (getUserId != null)
//            userIdTxt.setText(getUserId);

//        bioProp();

        bioMatImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bioProp();
            }
        });

        androidx.biometric.BiometricManager biometricManager;
        biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {

            // this means we can use biometric sensor
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;

            // this means that the device doesn't have fingerprint sensor
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:

                break;

            // this means that biometric sensor is not available
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                msgtex.setText("The biometric sensor is currently unavailable");
//                loginbutton.setVisibility(View.GONE);
                break;

            // this means that the device doesn't contain your fingerprint
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//                msgtex.setText("Your device doesn't have fingerprint saved,please check your security settings");
//                loginbutton.setVisibility(View.GONE);
                break;
        }

        userLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checking.isConnectingToInternet(LoginActivity.this)) {

                    if (validateData()) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("user_id", userIdTxt.getText().toString().trim());
                            jsonObject.put("password", passwordTxt.getText().toString().trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loginUser(jsonObject);
                    }

                } else {
                    SweetAlertDialog dialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    dialog.setCancelable(false);
                    dialog.setTitle("Network Error");
                    dialog.setContentText("Please check Your Network Connection");
                    dialog.setConfirmText("OK");
                    dialog.setConfirmClickListener(null);
                    dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    dialog.show();
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

    private boolean validateData() {
        if (userIdTxt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your user id", Toast.LENGTH_SHORT).show();
            return false;
        } else if (userIdTxt.getText().toString().length() != 10) {
            Toast.makeText(this, "Please enter valid user id", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordTxt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordTxt.getText().toString().length() < 8 && passwordTxt.getText().toString().length() > 12) {
            Toast.makeText(this, "Please enter password between 8 to 12 chars", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loginUser(JSONObject userData) {
        final SweetAlertDialog dialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        JsonObject userObj = new JsonParser().parse(userData.toString()).getAsJsonObject();
        Retrofit retrofit = HttpHelpers.getInstance();
        apiInterface = retrofit.create(ApiInterface.class);

        Call<JsonObject> userCall = apiInterface.loginUser(userObj);
        userCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, final Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        //MAIN LOGIN
                        JsonObject data = response.body().get("data").getAsJsonObject();
                        JsonObject userDetails = data.get("user_details").getAsJsonObject();
                        String token = userDetails.get("token").getAsString();

                        if (token.isEmpty()) {
                            dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            dialog.setTitle("Error!");
                            dialog.setContentText(data.get("message").getAsString());
                            dialog.setConfirmText("Ok!");
                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setTitle("Success!");
                            dialog.setContentText(data.get("message").getAsString());
                            dialog.setConfirmText("Ok!");
                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    AppPreferences.getInstance(getApplicationContext()).put(AppPreferences.Key.TOKEN, token);
                                    AppPreferences.getInstance(getApplicationContext()).put(AppPreferences.Key.PRIMARY_UPI, userDetails.get("primary_upi_id").getAsString());
                                    AppPreferences.getInstance(getApplicationContext()).put(AppPreferences.Key.MERCH_NAME, userDetails.get("merchant_name").getAsString());
                                    AppPreferences.getInstance(getApplicationContext()).put(AppPreferences.Key.USER_ID, userIdTxt.getText().toString().trim());
                                    AppPreferences.getInstance(getApplicationContext()).put(AppPreferences.Key.LOGGED, true);

                                    String userID = AppPreferences.getInstance(getApplicationContext()).getString(AppPreferences.Key.USER_ID);
                                    Log.d("TAG_ID", userID + "");

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }

                        Log.d("TAG_APP", response.body().toString());
                    } else {
                        //NULL BODY
                        dialog.setTitle("Something Went Wrong");
                        dialog.setConfirmText("Done");
                        dialog.setConfirmClickListener(null);
                        dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        Log.d("TAG_APP", "NULL BODY");
                    }
                } else {
                    //RESPONSE IS NOT SUCCESSFUL
                    //NULL BODY
                    dialog.setTitle("Something Went Wrong");
                    dialog.setConfirmText("Done");
                    dialog.setConfirmClickListener(null);
                    dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    Log.d("TAG_APP", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //ON FAILURE
                Log.d("TAG_APP", t.toString());
                t.getStackTrace();
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
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
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
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("UPI QR").setSubtitle("Easy Access")
                .setDescription("Use your fingerprint to login ").setNegativeButtonText("Cancel").build();
        biometricPrompt.authenticate(promptInfo);
    }
}