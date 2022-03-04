package com.anand.upiqr.Pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Transactions {
    @SerializedName("data")
    @Expose
    private List<Transaction> data = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Transaction> getData() {
        return data;
    }

    public void setData(List<Transaction> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
