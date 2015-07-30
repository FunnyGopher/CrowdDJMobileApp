package com.github.funnygopher.crowddjmobileapp.playlist;

import android.util.Xml;

import com.github.funnygopher.crowddjmobileapp.Song;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PlaylistXMLParser {

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readPlaylist(parser);
        } finally {
            in.close();
        }
    }

    private List readPlaylist(XmlPullParser parser) throws IOException, XmlPullParserException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, null, "playlist");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("song")) {
                entries.add(readSong(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Song readSong(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "song");
        String title = "";
        String artist = "";
        String uri = "";
        int votes = 0;

        while(parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if(name.equals("title")) {
                title = readTag(parser, "title");
            } else if (name.equals("artist")) {
                artist = readTag(parser, "artist");
            } else if (name.equals("uri")) {
                uri = readTag(parser, "uri");
            } else if (name.equals("votes")) {
                votes = Integer.parseInt(readTag(parser, "votes"));
            } else {
                skip(parser);
            }
        }

        return new Song(title, artist, uri, votes);
    }

    private String readTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, tagName);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, tagName);
        return title;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
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
}
