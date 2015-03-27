package com.danish.uberproject.utils;

/**
 * Created by darshad on 3/23/15.
 */
public class IOUtil {

    private IOUtil () {}

    /**
     * Checks to see if text input is not null, is not empty, and and doesn't consist of just white
     * spaces
     * @param text The string input
     * @return boolean true if text matches the criteria above, false otherwise
     */
    public static boolean isValidInput (String text) {
        return (text != null && !text.isEmpty() && !text.trim().isEmpty());
    }
}
