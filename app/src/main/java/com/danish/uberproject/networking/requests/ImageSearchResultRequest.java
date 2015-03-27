package com.danish.uberproject.networking.requests;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by darshad on 3/21/15.
 */
public class ImageSearchResultRequest<T> extends Request<T> {

    private final Class clazz;
    private final Listener successListener;
    private static final String baseURL =  "https://ajax.googleapis.com/ajax/services/search/" +
            "images?v=1.0&rsz=8";

    public ImageSearchResultRequest(String urlParams, Class<T> clazz, ErrorListener errorListener,
                                    Listener<T> listener) {
        super(Method.GET, urlParams,errorListener);
        this.successListener = listener;
        this.clazz = clazz;
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {
            Gson gson = new Gson();
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(gson.fromJson(json, clazz),
                                            HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        successListener.onResponse(response);
    }

    @Override
    public String getUrl() {
        return baseURL + super.getUrl();
    }
}
