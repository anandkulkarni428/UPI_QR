package com.anand.upiqr.Utils;

public class Transaction {
    int id;
    String transactionId;
    String upiId;
    String amount;
    String firstName;
    String lastName;

    public Transaction() {
    }

    public Transaction(int id, String transactionId, String upiId, String amount, String firstName, String lastName) {
        this.id = id;
        this.transactionId = transactionId;
        this.upiId = upiId;
        this.amount = amount;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
