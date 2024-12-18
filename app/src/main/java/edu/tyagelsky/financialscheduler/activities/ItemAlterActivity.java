package edu.tyagelsky.financialscheduler.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import edu.tyagelsky.financialscheduler.R;
import edu.tyagelsky.financialscheduler.Transaction;
import edu.tyagelsky.financialscheduler.TransactionCategoryFactory;
import edu.tyagelsky.financialscheduler.TransactionCategoryWrapper;
import edu.tyagelsky.financialscheduler.TypeOfOperation;
import edu.tyagelsky.financialscheduler.db.DatabaseHelper;
import edu.tyagelsky.financialscheduler.db.TransactionDao;

public class ItemAlterActivity  extends AppCompatActivity
{
    private Transaction baseTransaction;

    private String newTrName;
    private TransactionCategoryFactory.TransactionCategory newTrCategory;
    private Double newTrRate;
    private LocalDate newTrDate;


    private EditText inputTrName;
    private EditText inputTrRate;
    private TextView textChosenDate;
    private Spinner spinnerTrCategory;
    private Switch switchAlterTrDate;
    private Switch switchTrIncomeOrExpense;
    private Button buttonAlterTr;
    private Button buttonRestoreForm;

    private List<TransactionCategoryWrapper> incomeCatWrapperList;
    private List<TransactionCategoryWrapper> expenseCatWrapperList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_alter_layout);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        baseTransaction = getIntent().getParcelableExtra("BASE_TRANSACTION", Transaction.class);

        incomeCatWrapperList = TransactionCategoryWrapper.convertFromCategoryList(TransactionCategoryFactory.getCategoriesByTypeOfOperation(TypeOfOperation.INCOME));
        expenseCatWrapperList = TransactionCategoryWrapper.convertFromCategoryList(TransactionCategoryFactory.getCategoriesByTypeOfOperation(TypeOfOperation.EXPENSE));

        inputTrName = findViewById(R.id.transaction_name);
        inputTrRate = findViewById(R.id.transaction_rate);
        textChosenDate = findViewById(R.id.chosen_date);
        spinnerTrCategory = findViewById(R.id.transaction_category);
        switchAlterTrDate = findViewById(R.id.alter_date_switch);
        switchTrIncomeOrExpense = findViewById(R.id.switch_type_of_transaction);
        buttonAlterTr = findViewById(R.id.btn_alter_transaction);
        buttonRestoreForm = findViewById(R.id.btn_restore_form);

        newTrName = baseTransaction.getTrName();
        newTrCategory = baseTransaction.getTrCategory();
        newTrRate = baseTransaction.getTrRate();
        newTrDate = baseTransaction.getTrDate();

        inputTrName.setText(baseTransaction.getTrName());
        inputTrRate.setText(String.valueOf(baseTransaction.getTrRate()));
        textChosenDate.setText(baseTransaction.getTrDate().toString());
        spinnerTrCategory.setSelection(incomeCatWrapperList.indexOf(new TransactionCategoryWrapper(baseTransaction.getTrCategory())));
        switchAlterTrDate.setChecked(false);
        switchTrIncomeOrExpense.setChecked(baseTransaction.getTypeOfOperation().equals(TypeOfOperation.EXPENSE));


        final MaterialDatePicker<Long> matDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build();


        matDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            newTrDate = LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1, // Month is 0-indexed
                    calendar.get(Calendar.DAY_OF_MONTH));

            textChosenDate.setText(newTrDate.toString());

        });

        matDatePicker.addOnCancelListener( cancel ->
        {
            switchAlterTrDate.setChecked(false);
        });

        matDatePicker.addOnNegativeButtonClickListener( selection ->{
            switchAlterTrDate.setChecked(false);
        });




        switchAlterTrDate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                matDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        });


        final ArrayAdapter<TransactionCategoryWrapper> trCategoryInputAdapterIncome = new ArrayAdapter<>(
                this,
                //android.R.layout.simple_spinner_item,
                R.layout.spinner_layout,
                incomeCatWrapperList);

        final ArrayAdapter<TransactionCategoryWrapper> trCategoryInputAdapterExpense = new ArrayAdapter<>(
                this,
                //android.R.layout.simple_spinner_item,
                R.layout.spinner_layout,
                expenseCatWrapperList);

        trCategoryInputAdapterIncome.setDropDownViewResource(
                //android.R.layout.simple_spinner_dropdown_item);
                R.layout.spinner_dropdown_layout);
        trCategoryInputAdapterExpense.setDropDownViewResource(
                //android.R.layout.simple_spinner_dropdown_item);
                R.layout.spinner_dropdown_layout);

        spinnerTrCategory.setAdapter(trCategoryInputAdapterIncome);

        spinnerTrCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                newTrCategory = ((TransactionCategoryWrapper) adapterView.getSelectedItem()).getTrCategory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        switchTrIncomeOrExpense.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                spinnerTrCategory.setAdapter(trCategoryInputAdapterExpense);
                spinnerTrCategory.setSelection(expenseCatWrapperList.indexOf(new TransactionCategoryWrapper(TransactionCategoryFactory.defaultCategoryForExpense)));
            }
            else {
                spinnerTrCategory.setAdapter(trCategoryInputAdapterIncome);
                spinnerTrCategory.setSelection(incomeCatWrapperList.indexOf(new TransactionCategoryWrapper(TransactionCategoryFactory.defaultCategoryForIncome)));
            }
        });

        buttonAlterTr.setOnClickListener(v ->
        {
            if(inputTrName.getText().toString().trim().isBlank())
            {
                Toast.makeText(this, "Fill in the transaction name", Toast.LENGTH_SHORT).show();
                newTrName = null;
            }else
                newTrName = inputTrName.getText().toString().trim();


            if(inputTrRate.getText().toString().trim().isBlank())
            {
                Toast.makeText(this,"Fill in the transaction rate",Toast.LENGTH_SHORT).show();
                newTrRate = null;
            }
            else
                newTrRate = Double.valueOf(inputTrRate.getText().toString().trim());


            if(newTrName != null && newTrRate != null)
            {
                baseTransaction.updateName(newTrName);
                baseTransaction.updateCategory(newTrCategory);
                baseTransaction.updateRate(newTrRate);
                baseTransaction.updateDate(newTrDate);

                TransactionDao.updateTransaction(baseTransaction, new DatabaseHelper(this));
                Log.d("I", "IN SECOND ACTIVITY");
                Log.d("I", String.valueOf(baseTransaction.getTrRate()));
                Log.d("I", String.valueOf(baseTransaction));
                Log.d("I", String.valueOf(baseTransaction.getObjectID()));
                final Intent intent = new Intent();
                intent.putExtra("POSITION", getIntent().getIntExtra("POSITION", -1));
                setResult(RESULT_OK, intent);
                finish(); // Close this activity and return to MainActivity
            }
        });

        buttonRestoreForm.setOnClickListener(v ->
        {
            newTrName = baseTransaction.getTrName();
            newTrCategory = baseTransaction.getTrCategory();
            newTrRate = baseTransaction.getTrRate();
            newTrDate = baseTransaction.getTrDate();


            inputTrName.setText(baseTransaction.getTrName());
            inputTrRate.setText(String.valueOf(baseTransaction.getTrRate()));
            textChosenDate.setText(baseTransaction.getTrDate().toString());
            spinnerTrCategory.setSelection(incomeCatWrapperList.indexOf(new TransactionCategoryWrapper(baseTransaction.getTrCategory())));
            switchAlterTrDate.setChecked(false);
            switchTrIncomeOrExpense.setChecked(baseTransaction.getTypeOfOperation().equals(TypeOfOperation.EXPENSE));
        });
    }
}
