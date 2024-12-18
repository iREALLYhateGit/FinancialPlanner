package edu.tyagelsky.financialscheduler.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.tyagelsky.financialscheduler.Transaction;
import edu.tyagelsky.financialscheduler.TransactionCategoryFactory;
import edu.tyagelsky.financialscheduler.TypeOfOperation;
import edu.tyagelsky.financialscheduler.exceptions.CategoryAlreadyExistsException;

public final class CategoryDao
{

    public static long insertCategory(final TransactionCategoryFactory.TransactionCategory trCategory,
                                      final DatabaseHelper dbHelper)
    {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COLUMN_CAT_NAME, trCategory.getName());
        values.put(DatabaseHelper.COLUMN_CAT_TYPE, trCategory.getTypeOfOperation().toString());

        final long id = db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
        db.close();
        return id;
    }

    public static List<TransactionCategoryFactory.TransactionCategory> getAllCategories(final DatabaseHelper dbHelper)
    {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final List<TransactionCategoryFactory.TransactionCategory> categories = new ArrayList<>();
        final Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORIES, null, null, null, null, null, null);

        if (cursor.moveToFirst())
        {
            do {
                final String catName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_NAME));
                final String catTypeOfOperation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_TYPE));

                final TransactionCategoryFactory.TransactionCategory trCategory
                        = TransactionCategoryFactory.obtainCategory(
                            catName, TypeOfOperation.valueOf(catTypeOfOperation.toUpperCase()));

                categories.add(trCategory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    // Delete a transaction
    public static void deleteCategory(final TransactionCategoryFactory.TransactionCategory trCategory,
                                      final DatabaseHelper dbHelper)
    {
        final String catName = trCategory.getName();
        final String catType = trCategory.getTypeOfOperation().toString();
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_CATEGORIES,
                DatabaseHelper.COLUMN_CAT_NAME + " = " + catName
                        + " and " + DatabaseHelper.COLUMN_CAT_TYPE + " = " + catType,
                null);
        db.close();
    }
}
