package com.pinder.app.utils;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DisableButton {
    public static void disableButton(Button button){
        button.setVisibility(View.INVISIBLE);
        button.setLayoutParams(new LinearLayout.LayoutParams(0,0));
    }
    public static void disableButton(ImageView button){
        button.setVisibility(View.INVISIBLE);
        button.setLayoutParams(new LinearLayout.LayoutParams(0,0));
    }

    public static void disableButtonInDebug(Button button){
    }
    public static void disableButtonInDebug(ImageView button){
    }
}
