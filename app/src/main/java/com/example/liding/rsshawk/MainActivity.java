package com.example.liding.rsshawk;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.liding.rsshawk.sync.RssReaderAdapter;

public class MainActivity extends AppCompatActivity implements MainActivityInterface {
    private final String FRAGMENT_TAG = "FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RssListFragment(), FRAGMENT_TAG)
                .addToBackStack("list")
                .commit();
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        RssReaderAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onReadMore(String url) {

        RssDetailFragment detailFragment = new RssDetailFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        detailFragment.setArguments(args);

        getFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, detailFragment, FRAGMENT_TAG)
            .commit();
    }

    @Override
    public void onBackPressed() {
        //hack because popBackStack() don't work
        if(getFragmentManager().findFragmentByTag(FRAGMENT_TAG) instanceof RssListFragment) {
            super.onBackPressed();
        }

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RssListFragment(), FRAGMENT_TAG)
                .commit();
        } else {
            super.onBackPressed();
        }
    }
}
