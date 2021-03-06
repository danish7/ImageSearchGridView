package com.danish.uberproject.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import com.danish.uberproject.R;
import com.danish.uberproject.ui.fragments.GridFragment;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This method gets called when the user presses the search button. The intent containing
     * the search query is delivered to this method because of the singleTop flag specified in
     * the Manifest file.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
           handleIntent(intent);
        }
    }

    /**
     * Grab the search query string from the intent delivered by the SearchView and let the grid
     * view fragment handle the downloading and displaying of images
     * @param intent The intent received from the Searchable
     */
    private void handleIntent (Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);
        FragmentManager fm = getSupportFragmentManager();
        GridFragment gridFragment =  (GridFragment) fm.findFragmentById(R.id.grid_fragment);
        gridFragment.handleSearchQuery(query);
        searchView.setQuery(query, false);
        searchView.clearFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

}
