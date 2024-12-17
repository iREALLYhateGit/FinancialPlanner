package edu.tyagelsky.financialscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class ItemGenerationActivity extends AppCompatActivity
{

    private EditText inputTrName;
    private EditText inputTrRate;
    private Spinner spinnerTrCategory;
    private Switch switchDate;
    private LocalDate trDate;
    private Switch switchTrIncomeOrExpense;
    private Button buttonCreateTr;
    private Button buttonClearForm;

    private List<TransactionCategoryWrapper> incomeCatWrapperList;
    private List<TransactionCategoryWrapper> expenseCatWrapperList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_generation_layout);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        inputTrName = findViewById(R.id.transaction_name);
        inputTrRate = findViewById(R.id.transaction_rate);
        spinnerTrCategory = findViewById(R.id.transaction_category);
        switchDate = findViewById(R.id.switch_date);
        switchTrIncomeOrExpense = findViewById(R.id.switch_type_of_transaction);
        buttonCreateTr = findViewById(R.id.btn_create_transaction);
        buttonClearForm = findViewById(R.id.btn_clear_form);

        final MaterialDatePicker<Long> matDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build();


        matDatePicker.addOnPositiveButtonClickListener(selection ->
        {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            trDate = LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1, // Month is 0-indexed
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        });

        matDatePicker.addOnCancelListener( cancel ->
        {
            switchDate.setChecked(false);
        });

        matDatePicker.addOnNegativeButtonClickListener( selection ->{
            switchDate.setChecked(false);
        });



        switchDate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                matDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        });


        incomeCatWrapperList = TransactionCategoryWrapper.convertFromCategoryList(TransactionCategoryFactory.getCategoriesByTypeOfOperation(TypeOfOperation.INCOME));
        expenseCatWrapperList = TransactionCategoryWrapper.convertFromCategoryList(TransactionCategoryFactory.getCategoriesByTypeOfOperation(TypeOfOperation.EXPENSE));

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

        spinnerTrCategory.setSelection(incomeCatWrapperList.indexOf(new TransactionCategoryWrapper(TransactionCategoryFactory.defaultCategoryForIncome)));

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

        buttonCreateTr.setOnClickListener(v ->
        {
            final String newTransactionName;

            if(inputTrName.getText().toString().trim().isBlank())
            {
                Toast.makeText(this, "Fill in the transaction name", Toast.LENGTH_SHORT).show();
                newTransactionName = null;
            }else
                newTransactionName = inputTrName.getText().toString().trim();


            final Double newTransactionRate;
            if(inputTrRate.getText().toString().trim().isBlank())
            {
                Toast.makeText(this,"Fill in the transaction rate",Toast.LENGTH_SHORT).show();
                newTransactionRate = null;
            }
            else
                newTransactionRate = Double.valueOf(inputTrRate.getText().toString().trim());


            if(newTransactionName != null && newTransactionRate != null)
            {
                if(trDate == null)
                {
                    trDate = LocalDate.now();
                }
                final Transaction newTransaction = Transaction.createNewTransaction(
                        newTransactionName,
                        ((TransactionCategoryWrapper) spinnerTrCategory.getSelectedItem()).getTrCategory(),
                        newTransactionRate,
                        LocalDate.now());

                final Intent resultIntent = new Intent();
                resultIntent.putExtra("NEW_ITEM", newTransaction); // Pass input back
                setResult(RESULT_OK, resultIntent);
                finish(); // Close this activity and return to MainActivity
            }
        });

        buttonClearForm.setOnClickListener(v ->
        {
            inputTrName.setText("");
            inputTrRate.setText("");
            spinnerTrCategory.setSelection(incomeCatWrapperList.indexOf(new TransactionCategoryWrapper(TransactionCategoryFactory.defaultCategoryForIncome)));
            switchDate.setChecked(false);
            trDate = null;
            switchTrIncomeOrExpense.setChecked(false);
        });
    }
}
