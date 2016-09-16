package com.example.liding.rsshawk;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class RssDetailFragment extends Fragment {

    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rss_detail, container, false);

        Bundle args = getArguments();
        String url = args.getString(
            "url", "https://placeholdit.imgix.net/~text?txtsize=13&txt=140%C3%97100&w=140&h=100"
        );

        mWebView = (WebView) view.findViewById(R.id.rss_detail);
        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.setWebViewClient(new WebViewClient() {} );
//        mWebView.setWebChromeClient(new WebChromeClient() {} );
        mWebView.loadUrl(url);
        return view;
    }
}
