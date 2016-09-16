package com.example.liding.rsshawk.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.liding.rsshawk.R;
import com.example.liding.rsshawk.data.RssContract;
import com.example.liding.rsshawk.sync.Parser.KotakuParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

public class RssReaderAdapter extends AbstractThreadedSyncAdapter {

    private String LOG_TAG = "RssReaderAdapter";

    public static final int SYNC_INTERVAL = 6;//60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private String mFinalUrl = "http://kotaku.com/rss";

    public RssReaderAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync");

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                // Instantiate the parser
                KotakuParser kotakuParser = new KotakuParser();
                List<KotakuParser.Item> items = null;
                try {
                    Vector<ContentValues> cVVector = new Vector<ContentValues>();
                    //download rss data
                    InputStream stream = downloadUrl(mFinalUrl);
                    items = kotakuParser.parse(stream);

                    for (KotakuParser.Item item : items) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("title", item.title);
                        contentValues.put("description", item.description);
                        contentValues.put("date", item.date);
                        contentValues.put("link", item.link);
                        contentValues.put("image", item.image);
                        cVVector.add(contentValues);
                    }

                    Log.d(LOG_TAG, "Rows count - " + cVVector.size());
                    if(cVVector.size() > 0) {
                        int deletedCount = getContext().getContentResolver().delete(
                            RssContract.RssEntry.CONTENT_URI, null, null
                        );

                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);

                        int insertedCount = getContext().getContentResolver().bulkInsert(
                            RssContract.RssEntry.CONTENT_URI, cvArray
                        );
                        Log.d(LOG_TAG, "Inserted count - " + insertedCount + " deleted count - " + deletedCount);
                    }

                    stream.close();
                }
                catch (Exception e) {
                }
            }
        });
        thread.start();
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
            (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account mAccount = new Account(
            context.getString(R.string.app_name),
            context.getString(R.string.sync_account_type)
        );
        if ( null == accountManager.getPassword(mAccount) ) {
            if (!accountManager.addAccountExplicitly(mAccount, "", null)) {
                return null;
            }

            onAccountCreated(mAccount, context);
        }
        return mAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        RssReaderAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
