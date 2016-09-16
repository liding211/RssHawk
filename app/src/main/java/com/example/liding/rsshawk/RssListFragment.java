package com.example.liding.rsshawk;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.liding.rsshawk.data.RssContract;
import com.example.liding.rsshawk.sync.RssReaderAdapter;

public class RssListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String LOG_TAG = "RssListFragment";
    private static final int RSS_LOADER = 0;
    private String mSortOrder = "ASC";

    private RecyclerView mRssListView;
    private RssAdapter mRssListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rss_list, container, false);

        mRssListAdapter = new RssAdapter(null, getActivity());

        mRssListView = (RecyclerView) rootView.findViewById(R.id.rss_list);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRssListView.setLayoutManager(mLayoutManager);

        mRssListView.setAdapter(mRssListAdapter);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = RssContract.RssEntry.CONTENT_URI;
        String sortOrder = RssContract.RssEntry._ID + " " + mSortOrder;
        CursorLoader loader = new CursorLoader(getActivity(), uri, RssContract.RssEntry.RSS_COLUMNS, null, null, sortOrder);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.refresh:
                RssReaderAdapter.syncImmediately(getActivity());
                return true;
            case R.id.asc_sort:
                mSortOrder = "ASC";
                getLoaderManager().restartLoader(RSS_LOADER, null, this);
                return true;
            case R.id.desc_sort:
                mSortOrder = "DESC";
                getLoaderManager().restartLoader(RSS_LOADER, null, this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
