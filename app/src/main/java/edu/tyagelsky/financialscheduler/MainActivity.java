package edu.tyagelsky.financialscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import edu.tyagelsky.financialscheduler.db.DatabaseHelper;
import edu.tyagelsky.financialscheduler.db.TransactionDao;


public class MainActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private TextView emptyView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private FloatingActionButton addTransactionButton;

    private ActivityResultLauncher<Intent> itemGenerationLauncher;
    private ActivityResultLauncher<Intent> updateTrLauncher;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        dbHelper = new DatabaseHelper(this);
        transactionList = TransactionDao.getAllTransactions(dbHelper);

        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);
        addTransactionButton = findViewById(R.id.add_item_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateTrLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        int position = result.getData().getIntExtra("POSITION", -1);
                        Log.d("I", String.valueOf(transactionAdapter.getItemCount()));
                        transactionAdapter.notifyItemChanged(position);
                        Log.d("I", String.valueOf(position));
                        Log.d("I", String.valueOf(transactionList.get(position).getTrRate()));
                        Log.d("I", String.valueOf(transactionList.get(position)));
                        Log.d("I", String.valueOf(transactionList.get(position).getObjectID()));
                    }
                }
        );

        // Set up adapter
        transactionAdapter = new TransactionAdapter(transactionList, position ->
        {
            TransactionDao.deleteTransaction(transactionList.get(position), dbHelper);
            transactionList.remove(position);

            checkOperationsList();
            transactionAdapter.notifyItemRemoved(position);
            transactionAdapter.notifyItemRangeChanged(position, transactionList.size());
            //adapter.notifyDataSetChanged();
        }, updateTrLauncher);
        recyclerView.setAdapter(transactionAdapter);

        itemGenerationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Get the new item from the result
                        final Transaction newTransaction = result.getData().getParcelableExtra("NEW_ITEM");
                        if (newTransaction != null)
                        {
                            TransactionDao.insertFinTransaction(newTransaction, dbHelper);
                            transactionList.add(newTransaction);
                            transactionAdapter.notifyItemInserted(transactionList.size() - 1);
                            // Scroll to the new item (optional)
                            recyclerView.scrollToPosition(transactionList.size() - 1);
                        }
                    }
                });

        addTransactionButton.setOnClickListener(v -> {
            final Intent intent = new Intent(MainActivity.this, ItemGenerationActivity.class);
            itemGenerationLauncher.launch(intent); // Use the launcher
        });

    }

    public final void checkOperationsList()
    {
        if(transactionList.isEmpty())
        {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

}