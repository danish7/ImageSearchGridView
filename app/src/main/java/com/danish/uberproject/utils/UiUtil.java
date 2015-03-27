package com.danish.uberproject.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.danish.uberproject.R;

/**
 * Created by darshad on 3/22/15.
 */
public class UiUtil {

    private UiUtil () {}

    /**
     * Creates a default Android alert dialog with provided title, and message
     * @param context The context in which to display the alert dialog
     * @param titleId The resource id of the title string
     * @param stringId The resource id of the dialog message string
     */
    public static void showSimpleDialog(Context context, int titleId, int stringId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(stringId).setNeutralButton(R.string.ok, null);
        builder.setTitle(titleId);
        builder.show();
    }

}
