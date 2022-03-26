package com.anand.upiqr.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anand.upiqr.Interface.ApiInterface;
import com.anand.upiqr.R;
import com.anand.upiqr.Utils.AppPreferences;
import com.anand.upiqr.Utils.ConnectionChecking;
import com.anand.upiqr.Utils.HttpHelpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    EditText firstNameTxt, merchantNametxt, userIdTxt, passwordTxt, upiIdTxt;
    TextView logTxt;
    Button userRegBtn;

    ApiInterface apiInterface;
    ConnectionChecking checking;
    String passRegex = ".*[\\^\\`\\~\\<\\,\\>\\\"\\'\\}\\{\\]\\[\\|\\)\\(\\;\\&\\*\\$\\%\\#\\@\\!\\:\\.\\/\\?\\\\\\+\\=\\-\\_\\ ].*";
    String upiIDRegex = "^[\\w.-]+@[\\w.-]+$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firstNameTxt = findViewById(R.id.first_name_text);
        merchantNametxt = findViewById(R.id.merchant_name_text);
        userIdTxt = findViewById(R.id.user_id_text);
        upiIdTxt = findViewById(R.id.upi_id_text);
        passwordTxt = findViewById(R.id.password_text);
        logTxt = findViewById(R.id.log_text);
        userRegBtn = findViewById(R.id.register_btn);

        checking = new ConnectionChecking();


        logTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        userRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checking.isConnectingToInternet(RegisterActivity.this)) {

                    if (validateData()) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("fullname", firstNameTxt.getText().toString().trim());
                            jsonObject.put("merchant_name", merchantNametxt.getText().toString().trim());
                            jsonObject.put("primary_upi_id", upiIdTxt.getText().toString().trim());
                            jsonObject.put("user_id", userIdTxt.getText().toString().trim());
                            jsonObject.put("password", passwordTxt.getText().toString().trim());
                            jsonObject.put("finger_auth", "false");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        registerUser(jsonObject);
                    }

                } else {
                    SweetAlertDialog dialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
    }

    private boolean validateData() {

        if (firstNameTxt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (merchantNametxt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your business name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (upiIdTxt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your upi id", Toast.LENGTH_SHORT).show();
            return false;
        } else if (upiIdTxt.getText().toString().matches(upiIDRegex)) {
            Toast.makeText(this, "Please enter valid upi id", Toast.LENGTH_SHORT).show();
            return false;
        } else if (userIdTxt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your user id", Toast.LENGTH_SHORT).show();
            return false;
        } else if (userIdTxt.getText().toString().length() != 10) {
            Toast.makeText(this, "Please enter valid user id", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordTxt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordTxt.getText().toString().matches(passRegex)) {
            Toast.makeText(this, "Please enter your password with at least one capital letter or one special char", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordTxt.getText().toString().length() < 8 && passwordTxt.getText().toString().length() > 12) {
            Toast.makeText(this, "Please enter password between 8 to 12 chars", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void registerUser(JSONObject userData) {

        final SweetAlertDialog dialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        JsonObject userObj = new JsonParser().parse(userData.toString()).getAsJsonObject();
        Retrofit retrofit = HttpHelpers.getInstance();
        apiInterface = retrofit.create(ApiInterface.class);

        Call<JsonObject> userCall = apiInterface.createUser(userObj);
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

                                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
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


}