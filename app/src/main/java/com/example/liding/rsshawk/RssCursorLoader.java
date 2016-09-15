package com.example.liding.rsshawk;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.liding.rsshawk.data.RssContract;

public class RssCursorLoader extends CursorLoader {

    public RssCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    public RssCursorLoader(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        Log.d("RssCursorLoader", "loadInBackground");
        Cursor cursor = getContext().getContentResolver().query(
            RssContract.RssEntry.CONTENT_URI, RssContract.RssEntry.RSS_COLUMNS, null, null, null
        );
        Log.d("RssCursorLoader", "loadInBackground cursor.size - " + cursor.getCount());
        return cursor;
    }

}
