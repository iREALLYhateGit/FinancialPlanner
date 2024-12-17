package edu.tyagelsky.financialscheduler;

import static android.app.Activity.RESULT_OK;
import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import edu.tyagelsky.financialscheduler.db.TransactionDao;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.OperationViewHolder>
{
    private final List<Transaction> finTransactions;
    private final OnDeleteClickListener deleteClickListener;

    private final ActivityResultLauncher<Intent> updateTrLauncher;

    interface OnDeleteClickListener
    {
        void onDeleteClick(int position);
    }

    public TransactionAdapter(List<Transaction> finTransactions,
                              OnDeleteClickListener deleteClickListener,
                              ActivityResultLauncher<Intent> updateTrLauncher)
    {
        this.finTransactions = finTransactions;
        this.deleteClickListener = deleteClickListener;
        this.updateTrLauncher = updateTrLauncher;
    }

    @Override
    public OperationViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_layout, parent, false);
        return new OperationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OperationViewHolder holder, int position)
    {
        final Transaction finTransaction = finTransactions.get(position);

        holder.operationName.setText(finTransaction.getTrName());
        holder.categoryName.setText(finTransaction.getTrCategory().getName());

        TransactionCategoryFactory.CategoryIconCode.setIcon(holder.categoryImage, finTransaction.getTrCategory());

        final String rateText = String.format
                (Locale.CANADA, "%.2f", finTransaction.getTrRate());

        if (finTransaction.getTypeOfOperation().equals(TypeOfOperation.INCOME)) {
            holder.rate.setText("+".concat(rateText).concat(" $"));
            holder.rate.setTextColor(Color.rgb(110  ,193,117));
        } else {
            holder.rate.setText("-".concat(rateText).concat(" $"));
            holder.rate.setTextColor(Color.WHITE);
        }

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v ->
                deleteClickListener.onDeleteClick(position));


        holder.itemView.findViewById(R.id.main_part).setOnClickListener( v ->
        {
            final Intent intent = new Intent(v.getContext(), ItemAlterActivity.class);
            Log.d("I", String.valueOf(finTransaction));
            Log.d("I", String.valueOf(finTransaction.getObjectID()));
            intent.putExtra("BASE_TRANSACTION", finTransaction); // Pass data to the new activity
            intent.putExtra("POSITION", position);
            updateTrLauncher.launch(intent);
            //((Context) mainActivity).startActivity(intent);

            //((Activity) v.getContext()).startActivityForResult(intent, RESULT_OK);

        });
    }

    @Override
    public int getItemCount()
    {
        return finTransactions.size();
    }

    public static class OperationViewHolder extends RecyclerView.ViewHolder
    {

        final TextView operationName, categoryName, rate;
        final ImageView categoryImage;
        final Button deleteButton;

        public OperationViewHolder(View itemView)
        {
            super(itemView);

            operationName = itemView.findViewById(R.id.transaction_name);
            categoryName = itemView.findViewById(R.id.transaction_category);
            rate = itemView.findViewById(R.id.transaction_rate);
            categoryImage = itemView.findViewById(R.id.category_icon);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}

