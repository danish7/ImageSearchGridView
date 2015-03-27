package com.danish.uberproject.networking.response.models;

import java.util.List;

/**
 * Created by darshad on 3/22/15.
 */
public class Cursor {
    
    private List<SearchPage> pages;
    private int currentPageIndex;

    public List<SearchPage> getPages() {
        return pages;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }
}
