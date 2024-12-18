package edu.tyagelsky.financialscheduler.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.tyagelsky.financialscheduler.R;
import edu.tyagelsky.financialscheduler.Transaction;
import edu.tyagelsky.financialscheduler.TransactionAdapter;
import edu.tyagelsky.financialscheduler.db.DatabaseHelper;
import edu.tyagelsky.financialscheduler.db.TransactionDao;


public class FilterTransactionsActivity extends AppCompatActivity
{
    private TextView textChooseDate;
    private TextView textChosenPeriod;
    private Spinner spinnerTypeOfDate;
    private RecyclerView recyclerView;
    private Button buttonBackToMain;

    private LocalDate dateToFilter;
    private LocalDate dateToFilter2;

    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;

    private ActivityResultLauncher<Intent> updateTrLauncher;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        dbHelper = new DatabaseHelper(this);
        transactionList = TransactionDao.getAllTransactions(dbHelper);

        textChooseDate = findViewById(R.id.choose_date);
        textChosenPeriod = findViewById(R.id.chosen_period);
        spinnerTypeOfDate = findViewById(R.id.type_of_date_spinner);
        recyclerView = findViewById(R.id.recycler_view);
        buttonBackToMain = findViewById(R.id.btn_back_to_main);

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

        buttonBackToMain.setOnClickListener( v ->
        {
            final Intent intent = new Intent(FilterTransactionsActivity.this, MainActivity.class);
            startActivity(intent);
        });

        final String typesOfFiltering [] = {"thisDay","date", "byYear", "byMonth", "dateAndPeriod"};

        final ArrayAdapter<String> typesOfFilteringAdapter = new ArrayAdapter<>(
                this,
                //android.R.layout.simple_spinner_item,
                R.layout.spinner_layout_filtering,
                typesOfFiltering);

        typesOfFilteringAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_layout_filtering);

        spinnerTypeOfDate.setAdapter(typesOfFilteringAdapter);

        dateToFilter = LocalDate.now();
        spinnerTypeOfDate.setSelection(0);
        textChosenPeriod.setText("Filtering: by today, i.e. by ".concat(dateToFilter.toString()));
        recyclerView.setAdapter(getTransactionAdapter(filterByDate(dateToFilter)));

        spinnerTypeOfDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                final String typeOfFiltering = (String) adapterView.getSelectedItem();

                switch (typeOfFiltering)
                {
                    case "thisDay":
                        invokeDatePickerAndFiltering();
                        break;
                    case "date":
                        invokeDatePickerAndFilteringByDate();
                        break;
                    case "byYear":
                        invokeDatePickerAndFilteringByYear();
                        break;
                    case "byMonth":
                        invokeDatePickerAndFilteringByMonth();
                        break;
                    case "dateAndPeriod":
                        invokeDatePickerAndFilteringByPeriod();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void invokeDatePickerAndFiltering()
    {
        dateToFilter = LocalDate.now();
        spinnerTypeOfDate.setSelection(0);
        textChosenPeriod.setText("Filtering: by today, i.e. by ".concat(dateToFilter.toString()));
        recyclerView.setAdapter(getTransactionAdapter(filterByDate(dateToFilter)));
    }

    private void invokeDatePickerAndFilteringByDate()
    {
        final MaterialDatePicker<Long> matDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build();

        matDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            dateToFilter = LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1, // Month is 0-indexed
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            textChosenPeriod.setText(dateToFilter.toString());
            recyclerView.setAdapter(getTransactionAdapter(filterByDate(dateToFilter)));
        });

        matDatePicker.addOnCancelListener( cancel ->
        {
            spinnerTypeOfDate.setSelection(0);
        });

        matDatePicker.addOnNegativeButtonClickListener( selection ->
        {
            spinnerTypeOfDate.setSelection(0);
        });

        matDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    private void invokeDatePickerAndFilteringByYear()
    {
        final MaterialDatePicker<Long> matDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build();

        matDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            dateToFilter = LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1, // Month is 0-indexed
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            textChosenPeriod.setText("Year number is ".concat(String.valueOf(dateToFilter.getYear())));
            recyclerView.setAdapter(getTransactionAdapter(filterByYear(dateToFilter)));
        });

        matDatePicker.addOnCancelListener( cancel ->
        {
            spinnerTypeOfDate.setSelection(0);
        });

        matDatePicker.addOnNegativeButtonClickListener( selection ->
        {
            spinnerTypeOfDate.setSelection(0);
        });

        matDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    private void invokeDatePickerAndFilteringByMonth()
    {
        final MaterialDatePicker<Long> matDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build();

        matDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            dateToFilter = LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1, // Month is 0-indexed
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            textChosenPeriod.setText("Month number is".concat(String.valueOf(dateToFilter.toString())));
            recyclerView.setAdapter(getTransactionAdapter(filterByMonth(dateToFilter)));
        });

        matDatePicker.addOnCancelListener( cancel ->
        {
            spinnerTypeOfDate.setSelection(0);
        });

        matDatePicker.addOnNegativeButtonClickListener( selection ->
        {
            spinnerTypeOfDate.setSelection(0);
        });

        matDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    private void invokeDatePickerAndFilteringByPeriod()
    {
        final MaterialDatePicker<Pair<Long, Long>> matDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Date")
                .build();

        matDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            if (selection != null)
            {
                final Calendar calendar1 = Calendar.getInstance();
                final Calendar calendar2 = Calendar.getInstance();
                calendar1.setTimeInMillis(selection.first);
                calendar2.setTimeInMillis(selection.second);

                dateToFilter = LocalDate.of(
                        calendar1.get(Calendar.YEAR),
                        calendar1.get(Calendar.MONTH) + 1, // Month is 0-indexed
                        calendar1.get(Calendar.DAY_OF_MONTH)
                );

                dateToFilter2 = LocalDate.of(
                        calendar2.get(Calendar.YEAR),
                        calendar2.get(Calendar.MONTH) + 1, // Month is 0-indexed
                        calendar2.get(Calendar.DAY_OF_MONTH)
                );
            }

            textChosenPeriod.setText(dateToFilter.toString().concat(dateToFilter2.toString()));
            recyclerView.setAdapter(getTransactionAdapter(filterByPeriod(dateToFilter, dateToFilter2)));
        });

        matDatePicker.addOnCancelListener( cancel ->
        {
            spinnerTypeOfDate.setSelection(0);
        });

        matDatePicker.addOnNegativeButtonClickListener( selection ->
        {
            spinnerTypeOfDate.setSelection(0);
        });

        matDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }


    private TransactionAdapter getTransactionAdapter(final List<Transaction> trList)
    {
        return new TransactionAdapter(trList, position ->
        {
            TransactionDao.deleteTransaction(trList.get(position), dbHelper);
            trList.remove(position);

            checkOperationsList();
            transactionAdapter.notifyItemRemoved(position);
            transactionAdapter.notifyItemRangeChanged(position, trList.size());
            //adapter.notifyDataSetChanged();
        }, updateTrLauncher);
    }

    public List<Transaction> filterByPeriod(final LocalDate firstLocalDate, final LocalDate secondLocalDate)
    {
        final List<Transaction> trList = new ArrayList<>();

        transactionList.stream().filter(tr -> tr.getTrDate().isAfter(firstLocalDate) && tr.getTrDate().isBefore(secondLocalDate)).forEach(trList::add);
        return trList;
    }

    public List<Transaction> filterByDate(final LocalDate firstLocalDate)
    {
        final List<Transaction> trList = new ArrayList<>();

        transactionList.stream().filter(tr -> tr.getTrDate().equals(firstLocalDate)).forEach(trList::add);
        return trList;
    }

    public List<Transaction> filterByYear(final LocalDate firstLocalDate)
    {
        final List<Transaction> trList = new ArrayList<>();

        transactionList.stream().filter(tr -> tr.getTrDate().getYear() == firstLocalDate.getYear()).forEach(trList::add);
        return trList;
    }

    public List<Transaction> filterByMonth(final LocalDate firstLocalDate)
    {
        final List<Transaction> trList = new ArrayList<>();

        transactionList.stream().filter(tr -> tr.getTrDate().getMonthValue() == firstLocalDate.getMonthValue()).forEach(trList::add);
        return trList;
    }

    public final void checkOperationsList()
    {
        if(transactionList.isEmpty())
        {
            recyclerView.setVisibility(View.INVISIBLE);
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}