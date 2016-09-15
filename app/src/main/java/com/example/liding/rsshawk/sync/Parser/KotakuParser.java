package com.example.liding.rsshawk.sync.Parser;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KotakuParser {
    // We don't use namespaces
    private static final String ns = null;

    public static class Item {
        public String title;
        public String link;
        public String description;
        public String date;
        public String image;

        private Item(String title, String description, String link, String date) {
            //get image for display
            try {
                List<String> imagesInText = findAllImages(description);
                Matcher matcher = Pattern.compile("src\\s*=\\s*([\\\"'])?([^ \\\\\"']*)").matcher(imagesInText.get(0));
                if (matcher.find()) {
                    this.image = matcher.group(2);
                } else {
                    this.image = null;
                }
            } catch (Exception e) {
                this.image = null;
                Log.d("!!!!!!!!!!!!!!!!!!!", "Shit happens! On " + title);
            }
            this.title = title;
            this.description = convertFromHtmlToText(description);
            this.link = link;
            this.date = date;
        }
    }

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readItems(parser);
        } finally {
            in.close();
        }
    }

    private List readItems(XmlPullParser parser) throws XmlPullParserException, IOException {
        List items = new ArrayList();

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("item")) {
                items.add(readItem(parser));
            }
        }
        return items;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private Item readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        String title = null;
        String description = null;
        String link = null;
        String date = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readText(parser);
            } else if (name.equals("description")) {
                description = readText(parser);
            } else if (name.equals("link")) {
                link = readText(parser);
            } else if (name.equals("pubDate")) {
                date = readText(parser);
            } else {
                skip(parser);
            }
        }
        return new Item(title, description, link, date);
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes description tags in the feed.
    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    // For the tags title and description, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Processes date tags in the feed.
    private String readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "pubDate");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "pubDate");
        return description;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private static List<String> findAllImages(String text) {
        ArrayList images = new ArrayList();
        if (text != null && !text.isEmpty()) {
            Matcher localMatcher =
                Pattern
                    .compile("<img\\s[^>]*?src\\s*=\\s*['\\\"]([^'\\\"]*?)['\\\"][^>]*?>")
                    .matcher(text);
            if (localMatcher.find()) {
                MatchResult localMatchResult = localMatcher.toMatchResult();
                if (localMatchResult != null) {
                    for (int i = 0; i < localMatchResult.groupCount(); i++) {
                        images.add(localMatchResult.group(i));
                    }
                }
            }
        }
        return images;
    }

    private static String removeImagesFromText(String text) {
        if (text != null && text.isEmpty())
            return text;
        return text.replaceAll("<img\\s[^>]*?src\\s*=\\s*['\\\"]([^'\\\"]*?)['\\\"][^>]*?>", "");
    }


    public static String convertFromHtmlToText(String html) {
        html = html.replaceAll("<([^a]*?)\\>"," ");//Removes all items in brackets
        html = html.replaceAll("<([^a]*?)\\\n"," ");//Must be undeneath
        html = html.replaceFirst("([^a]*?)\\>", " ");//Removes any connected item to the last bracket
        html = html.replaceAll("&nbsp;"," ");
        html = html.replaceAll("&amp;"," ");
        return html;
    }
}
