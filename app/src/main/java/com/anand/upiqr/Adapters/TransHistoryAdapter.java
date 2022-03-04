package com.anand.upiqr.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.anand.upiqr.Pojo.Transaction;
import com.anand.upiqr.R;
import com.anand.upiqr.databinding.HistoryItemBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransHistoryAdapter extends RecyclerView.Adapter<TransHistoryAdapter.ViewHolder> {
    Context context;
    List<Transaction> transactionList;
    HistoryItemBinding binding;
    Dialog dialog;
    DateTimeFormatter parser;
    DateTimeFormatter formatter;
    private final int QRcodeWidth = 500;
    private String TOTAL_AMOUNT = "00.0";
    private String UPI_ID = "";
    private String FIRST_NAME = "";
    private String LAST_NAME = "";
    private String TRANS_ID = "";

    public TransHistoryAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = HistoryItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull TransHistoryAdapter.ViewHolder holder, int position) {
//        final JsonObject object = transactionList.getAsJsonArray().get(position).getAsJsonObject();
        holder.nameTxt.setText(transactionList.get(position).getTransactedTo());
        holder.transTxt.setText(transactionList.get(position).getTransactionId());
        String dateString = transactionList.get(position).getTransactionDate();
        String timeString = transactionList.get(position).getTransactionTime();
        parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH);
        holder.dateTxt.setText(formatter.format(parser.parse(dateString)));
        parser = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS");
        formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        holder.timeTxt.setText(formatter.format(parser.parse(timeString)));
        holder.amountTxt.setText(context.getResources().getString(R.string.amount_lbl, transactionList.get(position).getAmount()));


        dialog = new Dialog(context);
        dialog.setTitle("User Info");
        TextView nameTxt, transTxt, upiTxt, amountTxt;
        ImageView cancelImg, qrImage;
        dialog.setContentView(R.layout.qr_details);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        nameTxt = dialog.findViewById(R.id.name_txt);
        transTxt = dialog.findViewById(R.id.trans_txt);
        upiTxt = dialog.findViewById(R.id.upi_id_text);
        amountTxt = dialog.findViewById(R.id.amount_txt);
        cancelImg = dialog.findViewById(R.id.cancel_img);
        qrImage = dialog.findViewById(R.id.qr_img);

        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        nameTxt.setText((transactionList.get(position).getTransactedTo()));
        upiTxt.setText(transactionList.get(position).getPrimaryUpiId());
        amountTxt.setText(transactionList.get(position).getAmount());

        String url = "upi://pay?pa="
                + transactionList.get(position).getPrimaryUpiId() + "&am=" + transactionList.get(position).getAmount()
                + "&pn=" + transactionList.get(position).getMerchantName() + "%20" + "" +
                "&cu=INR" + "&mode=02" + "&orgid=189999" +
                ""
                + "Q7lugo8mfJhDk6wIhANZkbXOWWR2lhJOH2Qs/OQRaRFD2oBuPCGtrMaVFR23t";

        Bitmap bitmap = this.textToImageEncode(url);


        qrImage.setImageBitmap(bitmap);


        qrImage.invalidate();

//        holder.rootLayout.setOnClickListener(view -> {
//            dialog.show();
//        });
    }

    @Override
    public int getItemCount() {
        Log.d("TAG_SIZE", transactionList.size() + "");
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt, transTxt, amountTxt, dateTxt, timeTxt;
        LinearLayout deleteBtn, nextBtn;
        CardView rootLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name_txt);
            transTxt = itemView.findViewById(R.id.trans_id_txt);
            amountTxt = itemView.findViewById(R.id.amount_txt);
            dateTxt = itemView.findViewById(R.id.date_txt);
            timeTxt = itemView.findViewById(R.id.time_txt);
            rootLayout = itemView.findViewById(R.id.root_layout);
        }
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
        return bitmap;
    }
}
