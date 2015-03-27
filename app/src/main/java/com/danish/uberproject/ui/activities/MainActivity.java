package com.danish.uberproject.ui.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.danish.uberproject.R;
import com.danish.uberproject.networking.requests.ImageSearchResultRequest;
import com.danish.uberproject.networking.response.models.Cursor;
import com.danish.uberproject.networking.response.models.ImageSearchResult;
import com.danish.uberproject.networking.response.models.ResponseData;
import com.danish.uberproject.providers.SearchSuggestionProvider;
import com.danish.uberproject.ui.adapters.GridAdapter;
import com.danish.uberproject.utils.IOUtil;
import com.danish.uberproject.utils.UiUtil;

import org.apache.http.HttpStatus;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 */
public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private GridAdapter gridAdapter;
    private String query;
    private GridView gridView;
    private ResponseData responseData;
    boolean itemsOffScreen = false;
    private ProgressDialog progressDialog;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.image_grid);
        gridAdapter = new GridAdapter(this);
        gridView.setAdapter(gridAdapter);
        gridView.setOnScrollListener(gridScrollListener);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_more_images));

    }

    /**
     * Scroll listener used to detect when to load more images from google image search API
     */
    private AbsListView.OnScrollListener gridScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState > 0 &&
                    view.getLastVisiblePosition() >= (view.getAdapter().getCount() - 2)) {
                loadMoreItems(responseData.getCursor());
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            if (totalItemCount > visibleItemCount) {
                itemsOffScreen = true;
            }
            if (visibleItemCount != 0 && !itemsOffScreen && responseData != null) {
                loadMoreItems(responseData.getCursor());
            }
        }
    };

    /**
     * Saves the search query using the Search suggestions content provider.
     * @param query The search string
     */
    private void saveSearchQuery (String query) {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
        suggestions.saveRecentQuery(query, null);
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

    private void handleIntent (Intent intent) {
        query = intent.getStringExtra(SearchManager.QUERY);
        if (gridAdapter != null) {
            gridAdapter.clearItems();
            itemsOffScreen = false;
        }
        if (IOUtil.isValidInput(query)) {
            saveSearchQuery(query);
            searchView.clearFocus();
            searchGoogleImages(query, "&start=0&q=");

        } else {
            UiUtil.showSimpleDialog(this, R.string.error, R.string.invalid_query);
        }
    }

    /**
     * Error listener for the Google image search request. This gets called if image search API
     * request returns unsuccessfully.
     */
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            UiUtil.showSimpleDialog(MainActivity.this, R.string.error, R.string.search_failed );
            Log.v(TAG, "Error Accessing Google Image API : " + volleyError);
        }
    };


    /**
     * Request more images from Google Image search API by using the appropriate page index
     * @param imageSearchCursor contains information on how to retrieve extra search results from
     *                          image API
     */
    public void loadMoreItems (Cursor imageSearchCursor) {
        int pageIndex = imageSearchCursor.getCurrentPageIndex();
        if (pageIndex != imageSearchCursor.getPages().size() - 1) {
            String params = "&start=" + ((pageIndex+1) * 8) + "&q=";
            searchGoogleImages(query, params);
        } else {
            Toast.makeText(this, getString(R.string.limit_reached), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Success listener for the Google image search request. This gets called if Image search API
     * request returns successfully.
     */
    Response.Listener<ImageSearchResult> successListener = new Response.Listener<ImageSearchResult>() {
        @Override
        public void onResponse(ImageSearchResult imageSearchResult) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (imageSearchResult != null &&
                    imageSearchResult.getResponseStatus().equals(String.valueOf(HttpStatus.SC_OK))) {
                changeBackground();
                responseData = imageSearchResult.getResponseData();
                gridAdapter.addImageUrls(responseData.getResults());
            } else {
                UiUtil.showSimpleDialog(MainActivity.this, R.string.error, R.string.search_failed );
                Log.v(TAG, "Google Image search returned nothing");

            }
        }
    };


    /**
     * Change the background color of the grid layout
     */
    private void changeBackground () {
        findViewById(R.id.grid_parent).
            setBackgroundColor(getResources().getColor(R.color.background_material_dark));
    }

    /**
     * API request to get Images from Google Image Search API
     * @param query The search query entered by the user
     * @param params Url params attached to the end of the base URL
     */
    private void searchGoogleImages(String query, String params) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String urlParams = params + encodedQuery;
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(new ImageSearchResultRequest(urlParams,
                    ImageSearchResult.class, errorListener, successListener));
            progressDialog.show();
        } catch (UnsupportedEncodingException e) {
            UiUtil.showSimpleDialog(MainActivity.this, R.string.error, R.string.search_failed );
        }

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
