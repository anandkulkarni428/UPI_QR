package com.anand.upiqr.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TransactionDataBaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "transactionManager";
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String KEY_ID = "id";
    private static final String KEY_UPI_ID = "upiId";
    private static final String KEY_TRANS_ID = "transId";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_AMOUNT = "amount";

    public TransactionDataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TRANS_ID + " TEXT," + KEY_FIRST_NAME + " TEXT," + KEY_LAST_NAME + " TEXT," + KEY_UPI_ID + " TEXT,"
                + KEY_AMOUNT + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);

        // Create tables again
        onCreate(db);
    }

    public void addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TRANS_ID, transaction.getTransactionId());
        values.put(KEY_FIRST_NAME, transaction.getFirstName());
        values.put(KEY_LAST_NAME, transaction.getLastName());
        values.put(KEY_UPI_ID, transaction.getUpiId());
        values.put(KEY_AMOUNT, transaction.getAmount());

        // Inserting Row
        db.insert(TABLE_TRANSACTIONS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    Transaction getTransaction(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_TRANSACTIONS, new String[]{KEY_ID,
                        KEY_TRANS_ID, KEY_FIRST_NAME, KEY_LAST_NAME, KEY_UPI_ID, KEY_AMOUNT}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Transaction transaction = new Transaction(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        // return contact
        return transaction;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<Transaction>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setId(Integer.parseInt(cursor.getString(0)));
                transaction.setTransactionId(cursor.getString(1));
                transaction.setFirstName(cursor.getString(2));
                transaction.setLastName(cursor.getString(3));
                transaction.setUpiId(cursor.getString(4));
                transaction.setAmount(cursor.getString(5));

                transactions.add(transaction);
            } while (cursor.moveToNext());
        }

        return transactions;
    }

    public int updateTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TRANS_ID, transaction.getTransactionId());
        values.put(KEY_FIRST_NAME, transaction.getFirstName());
        values.put(KEY_LAST_NAME, transaction.getLastName());
        values.put(KEY_UPI_ID, transaction.getUpiId());
        values.put(KEY_AMOUNT, transaction.getAmount());

        return db.update(TABLE_TRANSACTIONS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(transaction.getId())});
    }

    public void deleteTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, KEY_ID + " = ?",
                new String[]{String.valueOf(transaction.getId())});
        db.close();
    }

    public int getTransactionCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TRANSACTIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();

        // return count
        return cursor.getCount();
    }
}
