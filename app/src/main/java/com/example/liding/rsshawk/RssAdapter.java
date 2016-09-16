package com.example.liding.rsshawk;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liding.rsshawk.data.RssContract;
import com.squareup.picasso.Picasso;

import org.xml.sax.XMLReader;

public class RssAdapter extends RecyclerView.Adapter<RssAdapter.ViewHolder> {

    private Cursor mRssCursor;
    private Context mContext;

    public RssAdapter(Cursor cursor, Context context){
        mRssCursor = cursor;
        mContext = context;
    }

    @Override
    public RssAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
            .from(parent.getContext())
            .inflate(viewType, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RssAdapter.ViewHolder viewHolder, int position) {

        mRssCursor.moveToPosition(position);

        String title = mRssCursor.getString(
            mRssCursor.getColumnIndex(RssContract.RssEntry.COLUMN_TITLE)
        );
        String date = mRssCursor.getString(
            mRssCursor.getColumnIndex(RssContract.RssEntry.COLUMN_DATE)
        );
        String descriptionHtml = mRssCursor.getString(
            mRssCursor.getColumnIndex(RssContract.RssEntry.COLUMN_DESCRIPTION)
        );

        Spanned spannedDescription = Html.fromHtml(descriptionHtml, null, new Html.TagHandler()
        {
            public void handleTag(
                    boolean paramBoolean, String paramString, Editable paramEditable, XMLReader paramXMLReader
            ) {
                ClickableSpan[] arrayOfClickableSpan =
                        (ClickableSpan[])paramEditable.getSpans(0, paramEditable.length(), ClickableSpan.class);
                if (arrayOfClickableSpan != null) {
                    int i = arrayOfClickableSpan.length;
                    for (int j = 0; j < i; j++) {
                        ClickableSpan clickableSpan = arrayOfClickableSpan[j];
                        if (!(clickableSpan instanceof URLSpan))
                            continue;
                        paramEditable.setSpan(
                                new DescriptionUrlSpan(((URLSpan) clickableSpan).getURL()),
                                paramEditable.getSpanStart(clickableSpan),
                                paramEditable.getSpanEnd(clickableSpan),
                                Spanned.SPAN_POINT_MARK
                        );
                        paramEditable.removeSpan(clickableSpan);
                    }
                }
            }
        });

        try {
            Picasso.with(mContext)
                    .load(mRssCursor.getString(mRssCursor.getColumnIndex(RssContract.RssEntry.COLUMN_IMAGE)))
                    .into(viewHolder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.descriptionView.setText(spannedDescription);
        viewHolder.descriptionView.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.titleView.setText(title);
        viewHolder.dateView.setText(date);
    }

    @Override
    public int getItemCount() {
        return mRssCursor == null ? 0 : mRssCursor.getCount();
    }

    public Cursor swapCursor(Cursor cursor) {
        Log.d("!!!!!!!", "swapCursor");
        if (mRssCursor == cursor) {
            return null;
        }
        Cursor oldCursor = mRssCursor;
        this.mRssCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleView;
        public TextView descriptionView;
        public TextView dateView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.list_item_image);
            titleView = (TextView) view.findViewById(R.id.list_item_title);
            descriptionView = (TextView) view.findViewById(R.id.list_item_description);
            dateView = (TextView) view.findViewById(R.id.list_item_date);
        }
    }

    static public class DescriptionUrlSpan extends ClickableSpan {
        private final String url;

        public DescriptionUrlSpan(String url) {
            this.url = url;
        }

        public void onClick(View view) {
            Context context = view.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    Fragment DetailFragment = new RssDetailFragment();
                    Bundle localBundle = new Bundle();
                    localBundle.putString("url", this.url);
                    DetailFragment.setArguments(localBundle);
                    ((Activity) context).getFragmentManager()
                        .beginTransaction()
                        .addToBackStack("RssFeedFragment")
                        .replace(R.id.fragment_container, DetailFragment)
                        .commit();
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if((position % 2) > 0) {
            return R.layout.rss_list_item_v1;
        } else {
            return R.layout.rss_list_item;
        }
    }
}
