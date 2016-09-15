package com.example.liding.rsshawk.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class RssContract {

    public static final String CONTENT_AUTHORITY = "com.example.liding.rsshawk.app";
    public static final String PATH_RSS = "rss";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class RssEntry implements BaseColumns {

        public static final String[] RSS_COLUMNS = {
                RssContract.RssEntry._ID,
                RssContract.RssEntry.COLUMN_DATE,
                RssContract.RssEntry.COLUMN_DESCRIPTION,
                RssContract.RssEntry.COLUMN_LINK,
                RssContract.RssEntry.COLUMN_IMAGE,
                RssContract.RssEntry.COLUMN_TITLE
        };

        public static final String TABLE_NAME = "rss";

        public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_RSS).build();

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_IMAGE = "image";
    }
}
