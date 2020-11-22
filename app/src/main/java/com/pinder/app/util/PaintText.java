package com.pinder.app.util;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.widget.TextView;

import com.pinder.app.R;

public class PaintText {
    public static void paintLogo(TextView textView, String text) {
        TextPaint paint = textView.getPaint();
        float width = paint.measureText(text);
        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#E0232B"),
                        Color.parseColor("#F4427E"),
                        Color.parseColor("#713471"),
                        Color.parseColor("#213487"),
                        Color.parseColor("#2299F8"),
                        Color.parseColor("#20B89C"),
                        Color.parseColor("#54C634"),
                        Color.parseColor("#FFC107"),
                }, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);
        textView.setTextColor(Color.parseColor("#E0232B"));
    }
}
