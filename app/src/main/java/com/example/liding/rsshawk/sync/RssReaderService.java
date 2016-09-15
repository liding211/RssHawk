package com.example.liding.rsshawk.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class RssReaderService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static RssReaderAdapter sRssReaderAdapter;

    @Override
    public void onCreate() {
        Log.d("RssReaderService", "onCreate");
        RssReaderAdapter.initializeSyncAdapter(getApplicationContext());
        synchronized (sSyncAdapterLock) {
            if (sRssReaderAdapter == null) {
                sRssReaderAdapter = new RssReaderAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sRssReaderAdapter.getSyncAdapterBinder();
    }
}
