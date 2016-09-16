package com.example.liding.rsshawk;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liding.rsshawk.data.RssContract;
import com.squareup.picasso.Picasso;

import org.xml.sax.XMLReader;

public class RssAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView titleView;
        public final TextView descriptionView;
        public final TextView dateView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.list_item_image);
            titleView = (TextView) view.findViewById(R.id.list_item_title);
            descriptionView = (TextView) view.findViewById(R.id.list_item_description);
            dateView = (TextView) view.findViewById(R.id.list_item_date);
        }
    }

    public RssAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.rss_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String title = cursor.getString(cursor.getColumnIndex(RssContract.RssEntry.COLUMN_TITLE));
        String date = cursor.getString(cursor.getColumnIndex(RssContract.RssEntry.COLUMN_DATE));
        String descriptionHtml = cursor.getString(cursor.getColumnIndex(RssContract.RssEntry.COLUMN_DESCRIPTION));

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
            Picasso.with(context)
                .load(cursor.getString(cursor.getColumnIndex(RssContract.RssEntry.COLUMN_IMAGE)))
                .into(viewHolder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.descriptionView.setText(spannedDescription);
        viewHolder.descriptionView.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.titleView.setText(title);
        viewHolder.dateView.setText(date);
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
}
