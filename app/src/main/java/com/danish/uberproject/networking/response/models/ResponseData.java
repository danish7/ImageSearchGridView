package com.danish.uberproject.networking.response.models;

import java.util.List;

/**
 * Created by darshad on 3/21/15.
 */
public class ResponseData {

    private List<SearchResult> results;
    private Cursor cursor;


    public List<SearchResult> getResults() {
        return results;
    }

    public Cursor getCursor() {
        return cursor;
    }
}
