package com.danish.uberproject.networking.response.models;

/**
 * Created by darshad on 3/21/15.
 */
public class ImageSearchResult {

    private ResponseData responseData;
    private String responseStatus;
    private String responseDetails;


    public ResponseData getResponseData() {
        return responseData;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public String getResponseDetails() {
        return responseDetails;
    }

    @Override
    public String toString() {
        return "ImageSearchResult{" +
                "responseData=" + responseData +
                ", responseStatus='" + responseStatus + '\'' +
                ", responseDetails='" + responseDetails + '\'' +
                '}';
    }
}
