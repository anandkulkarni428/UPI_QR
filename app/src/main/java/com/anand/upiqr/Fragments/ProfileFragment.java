package com.anand.upiqr.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anand.upiqr.Activity.SplashActivity;
import com.anand.upiqr.Interface.ApiInterface;
import com.anand.upiqr.R;
import com.anand.upiqr.Utils.AppPreferences;
import com.anand.upiqr.Utils.ConnectionChecking;
import com.anand.upiqr.Utils.HttpHelpers;
import com.anand.upiqr.databinding.FragmentProfileBinding;
import com.google.gson.JsonObject;

import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    ApiInterface apiInterface;
    ConnectionChecking checking;
    private String userFirstLetter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        checking = new ConnectionChecking();
        getProfile();
        // Inflate the layout for this fragment
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppPreferences.getInstance(getActivity()).put(AppPreferences.Key.LOGGED, false);
                AppPreferences.getInstance(getActivity()).remove(AppPreferences.Key.TOKEN);
                AppPreferences.getInstance(getActivity()).remove(AppPreferences.Key.USER_ID);
                AppPreferences.getInstance(getActivity()).remove(AppPreferences.Key.MERCH_NAME);
                AppPreferences.getInstance(getActivity()).remove(AppPreferences.Key.PRIMARY_UPI);
                startActivity(new Intent(getActivity(), SplashActivity.class));
                getActivity().finish();

                Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void getProfile() {
        if (checking.isConnectingToInternet(getActivity())) {
            final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            dialog.setTitle("Loading...");
            dialog.setCancelable(false);
            dialog.show();

            Retrofit retrofit = HttpHelpers.getInstance();
            apiInterface = retrofit.create(ApiInterface.class);
            String userID = AppPreferences.getInstance(getActivity()).getString(AppPreferences.Key.USER_ID);
            Log.d("TAG_ID", userID + "");

            Call<JsonObject> profile = apiInterface.getProfile(userID);
            profile.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("TAG_ID", response.body().toString());
                    Log.d("TAG_RESPONSE_CODE", "code no : " + response.code());
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            JsonObject data = response.body().getAsJsonObject("data");
                            if (data.has("fullname")) {
                                binding.nameTxt.setText(data.get("fullname").getAsString());
                                userFirstLetter = (String) binding.nameTxt.getText().subSequence(0, 2);
                                binding.profileText.setText(userFirstLetter.toUpperCase(Locale.ROOT));
                            }
                            if (data.has("merchant_name")) {
                                binding.businessNameTxt.setText(data.get("merchant_name").getAsString());
                            }
                            if (data.has("user_id")) {
                                binding.userIdTxt.setText(data.get("user_id").getAsString());
                            }
                            if (data.has("primary_upi_id")) {
                                binding.primaryUpiTxt.setText(data.get("primary_upi_id").getAsString());
                            }
                            dialog.dismiss();

                        } else {
                            //NULL BODY
                            dialog.setTitle("Something Went Wrong 1");
                            dialog.setConfirmText("Done");
                            dialog.setConfirmClickListener(null);
                            dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            Log.d("TAG_APP", "NULL BODY");
                        }
                    } else {
                        //RESPONSE IS NOT SUCCESSFUL
                        //NULL BODY
                        dialog.setTitle("Something Went Wrong 2");
                        dialog.setConfirmText("Done");
                        dialog.setConfirmClickListener(null);
                        dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }
    }
}