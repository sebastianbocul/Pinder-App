package com.pinder.app.ui.dialogs;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesHelper {
    public static void setCurrentUserID(Context context, String myID) {
        SharedPreferences sp = context.getSharedPreferences("SP_USER", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Current_USERID", myID);
        editor.apply();
    }

    public static String getCurrentUserID(Context context) {
        SharedPreferences sp = context.getSharedPreferences("SP_USER", MODE_PRIVATE);
        String currentUser = sp.getString("Current_USERID", "None");
        return currentUser;
    }

}
