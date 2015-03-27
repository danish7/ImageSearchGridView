package com.danish.uberproject.providers;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Content provider used to access saved search queries
 */
public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "com.danish.uberproject.providersSearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
