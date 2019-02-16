package com.example.jmk2018.jmk_gowhere;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyboardHelper {

    private static final String LOG_TAG = "SoftKeyboardHelper";

    /**
     * Hidden constructor
     */
    private SoftKeyboardHelper() {
    }

    /**
     * getInputMethodManager
     */
    private static InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
    }

    /**
     * Hide the soft keyboard
     */
    public static void hide(Context context, View view) {

        if (context == null) {
            Log.w(LOG_TAG, "Cannot hide soft keyboard with null Context");
        }
        else if (view == null) {
            Log.w(LOG_TAG, "Cannot hide soft keyboard with null View");
        }
        else {
            InputMethodManager imm = getInputMethodManager(context);
            IBinder token = view.getApplicationWindowToken();
            imm.hideSoftInputFromWindow(token, 0);
        }
    }

    /**
     * Show the soft keyboard
     */
    public static void show(Context context) {

        if (context == null) {
            Log.w(LOG_TAG, "Cannot show soft keyboard with null Context");
        }
        else {
            InputMethodManager imm = getInputMethodManager(context);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }
}
