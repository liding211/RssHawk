package com.example.liding.rsshawk.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class RssProvider extends ContentProvider {

    private RssDbHelper mDbHelper;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    static final int RSS = 100;

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = RssContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, RssContract.PATH_RSS, RSS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new RssDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d("DB", "request query - " + uri.toString());
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case RSS:
                retCursor = mDbHelper.getReadableDatabase().query(
                    RssContract.RssEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), RssContract.RssEntry.CONTENT_URI);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Log.d("DB", "return retCursor - " + retCursor.getCount());
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RSS: {
                long _id = db.insert(RssContract.RssEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 ) {
                    returnUri = ContentUris.withAppendedId(RssContract.RssEntry.CONTENT_URI, _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String targetTable;
        switch (uriMatcher.match(uri)){
            case RSS:
                targetTable = RssContract.RssEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) {
            selection = "1";
        }

        int rowsDeleted = db.delete(targetTable, selection, selectionArgs);

        if (rowsDeleted != 0) {
            Log.d("DB", "delete - notifyAll");
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case RSS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        Uri insertUri = insert(RssContract.RssEntry.CONTENT_URI, value);
                        if (insertUri != null) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                Log.d("DB", "bulkInsert - notifyAll");
                getContext().getContentResolver().notifyChange(RssContract.RssEntry.CONTENT_URI, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
