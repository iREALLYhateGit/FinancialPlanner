package edu.tyagelsky.financialscheduler.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.tyagelsky.financialscheduler.Transaction;
import edu.tyagelsky.financialscheduler.TransactionCategoryFactory;

public final class TransactionDao
{

    public static long insertFinTransaction(final Transaction finTransaction,
                                            final DatabaseHelper dbHelper)
    {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COLUMN_NAME, finTransaction.getTrName());
        values.put(DatabaseHelper.COLUMN_CATEGORY, finTransaction.getTrCategory().toString());
        values.put(DatabaseHelper.COLUMN_RATE, finTransaction.getTrRate());
        values.put(DatabaseHelper.COLUMN_DATE, finTransaction.getTrDate().toString());

        final long id = db.insert(DatabaseHelper.TABLE_FINTRANSACTIONS, null, values);
        db.close();
        return id;
    }

    public static List<Transaction> getAllTransactions(final DatabaseHelper dbHelper)
    {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final List<Transaction> transactions = new ArrayList<>();
        final Cursor cursor = db.query(DatabaseHelper.TABLE_FINTRANSACTIONS, null, null, null, null, null, null);

        if (cursor.moveToFirst())
        {
            do {
                final String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                final String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY));
                final double rate = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATE));
                final String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));

                final Transaction transaction = Transaction.createNewTransaction(
                        name,
                        TransactionCategoryFactory.TransactionCategory.parseCategory(category),
                        rate,
                        LocalDate.parse(date)
                );
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return transactions;
    }

    // Delete a transaction
    public static void deleteTransaction(final Transaction transaction,
                                         final DatabaseHelper dbHelper)
    {
        final long objectID = transaction.getObjectID();
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_FINTRANSACTIONS, DatabaseHelper.COLUMN_ID + " = " + objectID,null);
        db.close();
    }

    public static void updateTransaction(final Transaction finTransaction,
                                         final DatabaseHelper dbHelper)
    {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        final ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COLUMN_NAME, finTransaction.getTrName());
        values.put(DatabaseHelper.COLUMN_CATEGORY, finTransaction.getTrCategory().toString());
        values.put(DatabaseHelper.COLUMN_RATE, finTransaction.getTrRate());
        values.put(DatabaseHelper.COLUMN_DATE, finTransaction.getTrDate().toString());

        db.update(DatabaseHelper.TABLE_FINTRANSACTIONS,
                values,
                DatabaseHelper.COLUMN_ID + " = " + finTransaction.getObjectID(),
                null);

        db.close();
    }
}
