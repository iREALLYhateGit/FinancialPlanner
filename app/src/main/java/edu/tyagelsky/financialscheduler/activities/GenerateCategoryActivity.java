package edu.tyagelsky.financialscheduler.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

import edu.tyagelsky.financialscheduler.R;
import edu.tyagelsky.financialscheduler.Transaction;
import edu.tyagelsky.financialscheduler.TransactionAdapter;
import edu.tyagelsky.financialscheduler.TransactionCategoryFactory;
import edu.tyagelsky.financialscheduler.TypeOfOperation;
import edu.tyagelsky.financialscheduler.db.CategoryDao;
import edu.tyagelsky.financialscheduler.db.DatabaseHelper;
import edu.tyagelsky.financialscheduler.db.TransactionDao;
import edu.tyagelsky.financialscheduler.exceptions.CategoryAlreadyExistsException;


public class GenerateCategoryActivity extends AppCompatActivity
{
    private EditText textNewCategoryName;
    private Switch switchNewCategoryType;
    private Button buttonCreateCategory;
    private Button buttonBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_generation);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        textNewCategoryName = findViewById(R.id.new_category_name);
        switchNewCategoryType = findViewById(R.id.new_category_type);
        buttonCreateCategory = findViewById(R.id.btn_create_category);
        buttonBackToMain = findViewById(R.id.btn_back_to_main_from_cat_generation);


        buttonCreateCategory.setOnClickListener(v ->
        {
            final String newCategoryName = textNewCategoryName.getText().toString();
            final TypeOfOperation newCategoryType = switchNewCategoryType.isChecked()?
                                                                    TypeOfOperation.EXPENSE:
                                                                    TypeOfOperation.INCOME;

            if(!newCategoryName.isBlank())
            {
                try
                {
                    final TransactionCategoryFactory.TransactionCategory trCategory = TransactionCategoryFactory.createCategory(newCategoryName, newCategoryType);
                    final DatabaseHelper databaseHelper = new DatabaseHelper(this);
                    CategoryDao.insertCategory(trCategory, databaseHelper);
                } catch (CategoryAlreadyExistsException e)
                {
                    Toast.makeText(this, "Such category is already exists", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(this, "Category name is blank", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBackToMain.setOnClickListener( v ->
        {
            final Intent intent = new Intent(GenerateCategoryActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}