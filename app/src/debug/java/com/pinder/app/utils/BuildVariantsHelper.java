package com.pinder.app.utils;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View;

public class BuildVariantsHelper {
    public static void disableButton(Button button){
    }
    public static void disableButton(ImageView button){
    }

    public static void disableButtonInDebug(Button button){
        button.setVisibility(View.GONE);
    }
    public static void disableButtonInDebug(ImageView button){
        button.setVisibility(View.GONE);
    }

    //for firebase testing
    public static void showLayout(LinearLayout linearLayout){
        linearLayout.setVisibility(View.VISIBLE);
    }
}
