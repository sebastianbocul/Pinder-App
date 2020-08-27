package com.pinder.app.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import android.view.View;

public class HideSoftKeyboard {
    public static void hideKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view==null){
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
