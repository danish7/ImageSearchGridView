package com.danish.uberproject.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by darshad on 3/27/15.
 */
public class GridFragment extends Fragment {

    private static final String TAG = GridFragment.class.getCanonicalName();
    private static final String QUERY = "com.danish.uberproject.ui.fragments.QUERY";

    private GridAdapter gridAdapter;
    private GridView gridView;
    boolean itemsOffScreen = false;
    private ProgressDialog progressDialog;
    private String query;
    private ResponseData responseData;

    private int scrollPosition = -1;


    public static GridFragment newInstance (String query) {
        GridFragment fragment = new GridFragment();
        Bundle args = new Bundle();
        args.putString(QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading_more_images));
        //if (scrollPosition == -1) {
        setRetainInstance(true);
        if (scrollPosition == -1) {
            Bundle bundle = getArguments();
            query = bundle.getString(QUERY);
            handleSearchQuery(query);
        }

           // gridView.smoothScrollToPosition(scrollPosition);
       // }
       // }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid_frag_layout, container, false);
        gridView = (GridView) view.findViewById(R.id.image_grid);
        gridAdapter = new GridAdapter(getActivity());
        gridAdapter.clearItems();
        gridView.setAdapter(gridAdapter);
        gridView.setOnScrollListener(gridScrollListener);
        return view;

    }


    /**
     * Saves the search query using the Search suggestions content provider.
     * @param query The search string
     */
    private void saveSearchQuery (String query) {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
                SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
        suggestions.saveRecentQuery(query, null);
    }

    private void handleSearchQuery (String query) {

        if (gridAdapter != null) {
            gridAdapter.clearItems();
            itemsOffScreen = false;
        }
        if (IOUtil.isValidInput(query)) {
            saveSearchQuery(query);
            searchGoogleImages(query, "&start=0&q=");
        } else {
            UiUtil.showSimpleDialog(getActivity(), R.string.error, R.string.invalid_query);
        }
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
     * Error listener for the Google image search request. This gets called if image search API
     * request returns unsuccessfully.
     */
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            UiUtil.showSimpleDialog(getActivity(), R.string.error, R.string.search_failed);
            Log.v(TAG, "Error Accessing Google Image API : " + volleyError);
        }
    };


    /**
     * API request to get Images from Google Image Search API
     * @param query The search query entered by the user
     * @param params Url params attached to the end of the base URL
     */
    private void searchGoogleImages(String query, String params) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String urlParams = params + encodedQuery;
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(new ImageSearchResultRequest(urlParams,
                    ImageSearchResult.class, errorListener, successListener));
            progressDialog.show();
        } catch (UnsupportedEncodingException e) {
            UiUtil.showSimpleDialog(getActivity(), R.string.error, R.string.search_failed );
        }

    }


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
            Toast.makeText(getActivity(), getString(R.string.limit_reached), Toast.LENGTH_SHORT).show();
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
                responseData = imageSearchResult.getResponseData();
                gridAdapter.addImageUrls(responseData.getResults());
            } else {
                UiUtil.showSimpleDialog(getActivity(), R.string.error, R.string.search_failed );
                Log.v(TAG, "Google Image search returned nothing");

            }
        }
    };




}
