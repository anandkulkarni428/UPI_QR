package com.anand.upiqr.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anand.upiqr.Adapters.TransHistoryAdapter;
import com.anand.upiqr.Interface.ApiInterface;
import com.anand.upiqr.Pojo.Transaction;
import com.anand.upiqr.Pojo.Transactions;
import com.anand.upiqr.Utils.AppPreferences;
import com.anand.upiqr.Utils.ConnectionChecking;
import com.anand.upiqr.Utils.HttpHelpers;
import com.anand.upiqr.databinding.FragmentHistoryBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class HistoryFragment extends Fragment {

    FragmentHistoryBinding binding;
    ApiInterface apiInterface;
    ConnectionChecking checking;
    Transactions transactions;
    List<Transaction> transactionsList;
    private JsonArray data;
    int transCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        checking = new ConnectionChecking();
        transactions = new Transactions();
        transactionsList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        binding.transRec.setLayoutManager(layoutManager);
        getTransactions();

        return view;
    }

    private void getTransactions() {
        if (checking.isConnectingToInternet(getActivity())) {
            final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            dialog.setTitle("Loading...");
            dialog.setCancelable(false);
            dialog.show();

            Retrofit retrofit = HttpHelpers.getInstance();
            apiInterface = retrofit.create(ApiInterface.class);
            String userID = AppPreferences.getInstance(getActivity()).getString(AppPreferences.Key.USER_ID);
            Log.d("TAG_ID", userID + "");

            Call<Transactions> profile = apiInterface.getTransactions(userID);
            profile.enqueue(new Callback<Transactions>() {
                @Override
                public void onResponse(Call<Transactions> call, Response<Transactions> response) {
                    Log.d("TAG_ID", response.body().toString());
                    Log.d("TAG_RESPONSE_CODE", "code no : " + response.code());
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
//                            transactions.getData();
                            transactions = response.body();
                            transactionsList = transactions.getData();
                            Collections.reverse(transactionsList);
                            TransHistoryAdapter transHistoryAdapter = new TransHistoryAdapter(getActivity(), transactionsList);
                            binding.transRec.setAdapter(transHistoryAdapter);
                            transCount = transactionsList.size();
                            binding.transCountTxt.setText(String.valueOf(transCount));
                            setLayout();
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
                public void onFailure(Call<Transactions> call, Throwable t) {
                    Log.d("TAG_HIST", t.toString());
                    t.getStackTrace();
                }
            });
        }
    }

    public void setLayout() {
        if (transactionsList.size() == 0) {
            binding.noTransLayout.setVisibility(View.VISIBLE);
            binding.transLayout.setVisibility(View.GONE);
        } else {
            binding.transLayout.setVisibility(View.VISIBLE);
            binding.noTransLayout.setVisibility(View.GONE);
        }
    }
}