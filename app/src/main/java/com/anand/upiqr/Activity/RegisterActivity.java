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

    EditText firstNameTxt, merchantNametxt, userIdTxt, passwordTxt,upiIdTxt;
    TextView logTxt;
    Button userRegBtn;

    ApiInterface apiInterface;
    ConnectionChecking checking;

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
        merchantNametxt = findViewById(R.id.merchant_name_text);
        userIdTxt = findViewById(R.id.user_id_text);
        upiIdTxt = findViewById(R.id.upi_id_text);
        passwordTxt = findViewById(R.id.password_text);
        logTxt = findViewById(R.id.log_text);
        userRegBtn = findViewById(R.id.register_btn);

        checking = new ConnectionChecking();

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
                if (checking.isConnectingToInternet(RegisterActivity.this)) {

//                    if (validation.validate()) {

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
//                    }

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
                        String tocken = response.body().get("token").getAsString();

                        if (tocken.isEmpty()){
                            dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            dialog.setTitle("Error!");
                            dialog.setContentText(response.body().get("message").getAsString());
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
                            dialog.setContentText(response.body().get("message").getAsString());
                            dialog.setConfirmText("Ok!");
                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    AppPreferences.getInstance(getApplicationContext()).put(AppPreferences.Key.TOKEN, response.body().get("token").getAsString());
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