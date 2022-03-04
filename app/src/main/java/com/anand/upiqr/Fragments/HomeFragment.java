package com.anand.upiqr.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anand.upiqr.Activity.HomeActivity;
import com.anand.upiqr.Activity.QR_Activity;
import com.anand.upiqr.Activity.RegisterActivity;
import com.anand.upiqr.Interface.ApiInterface;
import com.anand.upiqr.R;
import com.anand.upiqr.Utils.AppPreferences;
import com.anand.upiqr.Utils.ConnectionChecking;
import com.anand.upiqr.Utils.HttpHelpers;
import com.anand.upiqr.databinding.ActivityMainBinding;
import com.anand.upiqr.databinding.FragmentHomeBinding;
import com.google.android.material.chip.Chip;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    ApiInterface apiInterface;
    ConnectionChecking checking;
    String userID, AMOUNT, UPI_ID, MERCH_NAME, TRANS_ID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        checking = new ConnectionChecking();
        UPI_ID = AppPreferences.getInstance(getActivity()).getString(AppPreferences.Key.PRIMARY_UPI);
        binding.upiIdText.setText(UPI_ID);

        binding.amountChip.setOnCheckedChangeListener((group, i) -> {
            Chip chip = binding.amountChip.findViewById(i);
            if (chip != null)
                if (chip.isChecked()) {
                    binding.amountText.setText(chip.getText());
                } else {
                    binding.amountText.setText("");
                }
        });

        binding.qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checking.isConnectingToInternet(getActivity())) {

                    userID = AppPreferences.getInstance(getActivity()).getString(AppPreferences.Key.USER_ID);
                    AMOUNT = binding.amountText.getText().toString().trim();
                    MERCH_NAME = AppPreferences.getInstance(getActivity()).getString(AppPreferences.Key.MERCH_NAME);
                    Log.d("TAG_ID", userID + ":" + MERCH_NAME);

//                    if (validation.validate()) {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("transacted_to", binding.receivedFormText.getText().toString().trim());
                        jsonObject.put("notes", binding.notesText.getText().toString().trim());
                        jsonObject.put("primary_upi_id", UPI_ID);
                        jsonObject.put("user_id", userID);
                        jsonObject.put("amount", AMOUNT);
                        jsonObject.put("merchant_name", MERCH_NAME);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    postTransaction(jsonObject);
//                    }

                } else {
                    SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
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
        return view;
    }

    private void postTransaction(JSONObject userData) {
        final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        JsonObject userObj = new JsonParser().parse(userData.toString()).getAsJsonObject();
        Retrofit retrofit = HttpHelpers.getInstance();
        apiInterface = retrofit.create(ApiInterface.class);

        Call<JsonObject> userCall = apiInterface.postTransaction(userObj);
        userCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, final Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        //MAIN LOGIN
                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        JsonObject data = response.body().get("data").getAsJsonObject();
                        JsonObject transaction_details = data.get("transaction_details").getAsJsonObject();
                        TRANS_ID = transaction_details.get("transaction_id").getAsString();


                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), QR_Activity.class);
                        intent.putExtra("AMOUNT", AMOUNT);
                        intent.putExtra("UPI_ID", UPI_ID);
                        intent.putExtra("MERCH_NAME", MERCH_NAME);
                        intent.putExtra("TRANS_ID", TRANS_ID);
                        startActivity(intent);


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