package com.anand.upiqr.Interface;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("register")
    Call<JsonObject> createUser(@Body JsonObject jsonObject);
}
