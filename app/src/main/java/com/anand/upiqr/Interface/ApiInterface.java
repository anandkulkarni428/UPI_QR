package com.anand.upiqr.Interface;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("register")
    Call<JsonObject> createUser(@Body JsonObject jsonObject);

    @Headers({"Content-Type: application/json"})
    @POST("login")
    Call<JsonObject> loginUser(@Body JsonObject jsonObject);

    @GET("profile/{id}")
    Call<JsonObject> getProfile(@Path("id") String id);
}
