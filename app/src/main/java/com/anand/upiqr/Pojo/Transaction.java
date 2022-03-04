package com.anand.upiqr.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaction {
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("transacted_to")
    @Expose
    private String transactedTo;
    @SerializedName("transaction_date")
    @Expose
    private String transactionDate;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("transaction_time")
    @Expose
    private String transactionTime;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("merchant_name")
    @Expose
    private String merchantName;
    @SerializedName("primary_upi_id")
    @Expose
    private String primaryUpiId;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTransactedTo() {
        return transactedTo;
    }

    public void setTransactedTo(String transactedTo) {
        this.transactedTo = transactedTo;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrimaryUpiId() {
        return primaryUpiId;
    }

    public void setPrimaryUpiId(String primaryUpiId) {
        this.primaryUpiId = primaryUpiId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
