package com.example.liding.rsshawk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RssDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    private static String DATABASE_NAME = "RssHawk";

    public RssDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_RSS_TABLE = "CREATE TABLE " + RssContract.RssEntry.TABLE_NAME + " (" +
            RssContract.RssEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            RssContract.RssEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
            RssContract.RssEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
            RssContract.RssEntry.COLUMN_DATE + " TEXT, " +
            RssContract.RssEntry.COLUMN_IMAGE + " TEXT, " +
            RssContract.RssEntry.COLUMN_LINK + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_RSS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RssContract.RssEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
