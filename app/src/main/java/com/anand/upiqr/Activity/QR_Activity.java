package com.anand.upiqr.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anand.upiqr.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class QR_Activity extends AppCompatActivity {
    private final int QRcodeWidth = 500;
    private String TOTAL_AMOUNT = "00.0";
    private String UPI_ID = "";
    private String MERCH_NAME = "";
    private String TRANS_ID = "";
    ImageView qrImage, shareImage;
    TextView transTxt, upiIdTxt, amountTxt;
    Bitmap shareBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        qrImage = findViewById(R.id.qr_img);
        transTxt = findViewById(R.id.trans_id_text);
        upiIdTxt = findViewById(R.id.upi_id_text);
        amountTxt = findViewById(R.id.amount_text);
        TOTAL_AMOUNT = getIntent().getStringExtra("AMOUNT");
        UPI_ID = getIntent().getStringExtra("UPI_ID");
        MERCH_NAME = getIntent().getStringExtra("MERCH_NAME");
        TRANS_ID = getIntent().getStringExtra("TRANS_ID");

        transTxt.setText((MERCH_NAME));
        upiIdTxt.setText((UPI_ID));
        amountTxt.setText((getResources().getString(R.string.amount_paying, TOTAL_AMOUNT)));
        initViews();

    }

    private final Bitmap textToImageEncode(String Value) {
        BitMatrix bitMatrix = null;

        try {
            BitMatrix var10000 = (new MultiFormatWriter()).encode(Value, BarcodeFormat.QR_CODE, this.QRcodeWidth, this.QRcodeWidth, (Map) null);
            bitMatrix = var10000;
        } catch (IllegalArgumentException | WriterException var11) {
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        int y = 0;

        for (int var7 = bitMatrixHeight; y < var7; ++y) {
            int offset = y * bitMatrixWidth;
            int x = 0;

            for (int var10 = bitMatrixWidth; x < var10; ++x) {
                pixels[offset + x] = bitMatrix.get(x, y) ? Color.parseColor("#000000") : Color.parseColor("#ffffff");
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        shareBitmap = bitmap;
        return bitmap;
    }

    private final void initViews() {
        String url = "upi://pay?pa="
                + UPI_ID + "&am=" + TOTAL_AMOUNT
                + "&pn=" + MERCH_NAME + "%20" + "" +
                "&cu=INR" + "&mode=02" + "&orgid=189999" +
                ""
                + "Q7lugo8mfJhDk6wIhANZkbXOWWR2lhJOH2Qs/OQRaRFD2oBuPCGtrMaVFR23t";

        Bitmap bitmap = this.textToImageEncode(url);


        qrImage.setImageBitmap(bitmap);


        qrImage.invalidate();

    }

    public void share_bitMap_to_Apps() {

        Intent i = new Intent(Intent.ACTION_SEND);

        i.setType("image/*");
        i.setType("text/plain");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
    /*compress(Bitmap.CompressFormat.PNG, 100, stream);
    byte[] bytes = stream.toByteArray();*/


        i.putExtra(Intent.EXTRA_STREAM, getImageUri(this, shareBitmap));
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Payment");
        i.putExtra(android.content.Intent.EXTRA_TEXT, TRANS_ID+"\nHi scan this to complete your payment.");
        try {
            startActivity(Intent.createChooser(i, "My QR ..."));
        } catch (android.content.ActivityNotFoundException ex) {

            ex.printStackTrace();
        }


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, MERCH_NAME + TRANS_ID, null);
        return Uri.parse(path);
    }
}

