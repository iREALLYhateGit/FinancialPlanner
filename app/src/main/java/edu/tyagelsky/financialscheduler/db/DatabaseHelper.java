package edu.tyagelsky.financialscheduler.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "fin_transactions.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_FINTRANSACTIONS = "fin_transactions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_RATE = "rate";
    public static final String COLUMN_DATE = "date";

    public static final String TABLE_CATEGORIES = "tr_categories";
    public static final String COLUMN_CAT_ID = "id";
    public static final String COLUMN_CAT_NAME = "name";
    public static final String COLUMN_CAT_TYPE = "type_of_transaction";

    private static final String CREATE_TABLE_FINTRANSACTIONS =
            "CREATE TABLE " + TABLE_FINTRANSACTIONS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_CATEGORY + " TEXT, " +
                    COLUMN_RATE + " REAL, " +
                    COLUMN_DATE + " TEXT);";

    private static final String CREATE_TABLE_CATEGORIES =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    COLUMN_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CAT_NAME + " TEXT, " +
                    COLUMN_CAT_TYPE + " TEXT);";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FINTRANSACTIONS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FINTRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }
}
