package com.pinder.app.ui.dialogs;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesHelper {
    public static void setCurrentUserID(Context context, String myID) {
        SharedPreferences sp = context.getSharedPreferences("SP_USER", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("CURRENT_USERID", myID);
        editor.apply();
    }

    public static String getCurrentUserID(Context context) {
        SharedPreferences sp = context.getSharedPreferences("SP_USER", MODE_PRIVATE);
        String currentUser = sp.getString("CURRENT_USERID", "None");
        return currentUser;
    }

    public static void setCurrentProfilePicture(Context context, String profilePicture) {
        SharedPreferences sp = context.getSharedPreferences("SP_USER", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("MY_PROFILE_IMAGE", profilePicture);
        editor.apply();
    }

    public static String getCurrentProfilePicture(Context context) {
        SharedPreferences sp = context.getSharedPreferences("SP_USER", MODE_PRIVATE);
        String currentUser = sp.getString("MY_PROFILE_IMAGE", "default");
        return currentUser;
    }
}
