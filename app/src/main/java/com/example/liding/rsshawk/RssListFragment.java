package com.example.liding.rsshawk;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.liding.rsshawk.data.RssContract;

public class RssListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String LOG_TAG = "RssListFragment";
    private static final int RSS_LOADER = 0;

    private ListView mRssListView;
    private CursorAdapter mRssListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rss_list, container, false);

        mRssListAdapter = new RssAdapter(getActivity(), null, 0);

        mRssListView = (ListView) rootView.findViewById(R.id.rss_list);
        mRssListView.setAdapter(mRssListAdapter);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = RssContract.RssEntry.CONTENT_URI;
        String sortOrder = RssContract.RssEntry.COLUMN_DATE + " ASC";
        CursorLoader loader = new RssCursorLoader(getActivity(), uri, RssContract.RssEntry.RSS_COLUMNS, null, null, sortOrder);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished");
        mRssListAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
        mRssListAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(RSS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
}
