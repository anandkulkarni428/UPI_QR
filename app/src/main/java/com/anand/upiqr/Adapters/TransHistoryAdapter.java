package com.anand.upiqr.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import androidx.recyclerview.widget.RecyclerView;

import com.anand.upiqr.Activity.TransactionHistoryActivity;
import com.anand.upiqr.R;
import com.anand.upiqr.Utils.Transaction;
import com.anand.upiqr.Utils.TransactionDataBaseHandler;
import com.anand.upiqr.databinding.HistoryItemBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.List;
import java.util.Map;

public class TransHistoryAdapter extends RecyclerView.Adapter<TransHistoryAdapter.ViewHolder> {
    Context context;
    List<Transaction> transactionList;
    HistoryItemBinding binding;
    TransactionDataBaseHandler db;
    Dialog dialog;
    private final int QRcodeWidth = 500;
    private String TOTAL_AMOUNT = "00.0";
    private String UPI_ID = "";
    private String FIRST_NAME = "";
    private String LAST_NAME = "";
    private String TRANS_ID = "";

    public TransHistoryAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
        db = new TransactionDataBaseHandler(context);
    }

    @NonNull
    @Override
    public TransHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = HistoryItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull TransHistoryAdapter.ViewHolder holder, int position) {

        UPI_ID = transactionList.get(position).getUpiId();
        TOTAL_AMOUNT = transactionList.get(position).getAmount();
        FIRST_NAME = transactionList.get(position).getFirstName();
        LAST_NAME = transactionList.get(position).getLastName();

        holder.nameTxt.setText((FIRST_NAME + " " + LAST_NAME));
        holder.transTxt.setText(transactionList.get(position).getTransactionId());
        holder.deleteBtn.setOnClickListener(view -> {
            db.deleteTransaction(transactionList.get(position));
            TransactionHistoryActivity.transCountTXT.setText(String.valueOf(db.getTransactionCount()));
            notifyItemRangeChanged(0, transactionList.size());
            Log.d("TAG", "onBindViewHolder: delete item");
        });

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

        nameTxt.setText((FIRST_NAME + " " + LAST_NAME));
        transTxt.setText(transactionList.get(position).getTransactionId());
        upiTxt.setText(UPI_ID);
        amountTxt.setText(TOTAL_AMOUNT);

        String url = "upi://pay?pa="
                + UPI_ID + "&am=" + TOTAL_AMOUNT
                + "&pn=" + FIRST_NAME + "%20" + LAST_NAME +
                "&cu=INR" + "&mode=02" + "&orgid=189999" +
                ""
                + "Q7lugo8mfJhDk6wIhANZkbXOWWR2lhJOH2Qs/OQRaRFD2oBuPCGtrMaVFR23t";

        Bitmap bitmap = this.textToImageEncode(url);


        qrImage.setImageBitmap(bitmap);


        qrImage.invalidate();

        holder.nextBtn.setOnClickListener(view -> {
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt, transTxt;
        LinearLayout deleteBtn, nextBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name_txt);
            transTxt = itemView.findViewById(R.id.trans_txt);
            deleteBtn = itemView.findViewById(R.id.delete_layout);
            nextBtn = itemView.findViewById(R.id.next_layout);
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
