package com.danish.uberproject.networking.response.models;

/**
 * Created by darshad on 3/21/15.
 */
public class SearchResult {

    private String width;
    private String height;
    private String tbWidth;
    private String tbHeight;
    private String url;
    private String tbUrl;


    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getTbWidth() {
        return tbWidth;
    }

    public String getUrl() {
        return url;
    }

    public String getTbHeight() {
        return tbHeight;
    }

    public String getTbUrl () {
        return tbUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchResult that = (SearchResult) o;

        if (!tbUrl.equals(that.tbUrl)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tbUrl.hashCode();
    }
}
