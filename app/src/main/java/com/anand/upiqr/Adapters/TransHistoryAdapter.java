package com.anand.upiqr.Adapters;

import android.app.Dialog;
import android.content.Context;
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

import java.util.List;

public class TransHistoryAdapter extends RecyclerView.Adapter<TransHistoryAdapter.ViewHolder> {
    Context context;
    List<Transaction> transactionList;
    HistoryItemBinding binding;
    TransactionDataBaseHandler db;
    Dialog dialog;

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
        holder.nameTxt.setText((transactionList.get(position).getFirstName() + " " + transactionList.get(position).getLastName()));
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
        ImageView cancelImg;
        dialog.setContentView(R.layout.qr_details);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        nameTxt = dialog.findViewById(R.id.name_txt);
        transTxt = dialog.findViewById(R.id.trans_txt);
        upiTxt = dialog.findViewById(R.id.upi_id_text);
        amountTxt = dialog.findViewById(R.id.amount_txt);
        cancelImg = dialog.findViewById(R.id.cancel_img);

        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        nameTxt.setText((transactionList.get(position).getFirstName() + " " + transactionList.get(position).getLastName()));
        transTxt.setText(transactionList.get(position).getTransactionId());
        upiTxt.setText(transactionList.get(position).getUpiId());
        amountTxt.setText(transactionList.get(position).getAmount());
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
}
