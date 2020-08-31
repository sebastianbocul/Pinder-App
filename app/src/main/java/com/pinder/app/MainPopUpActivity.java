package com.pinder.app;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.pinder.app.models.Card;

public class MainPopUpActivity extends AppCompatActivity {
    private ImageView profileImage;
    private DatabaseReference db;
    private LinearLayout linearLayout;
    private TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pop_up);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        profileImage = findViewById(R.id.profileImage);
        nameTextView = findViewById(R.id.nameTextView);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        linearLayout = findViewById(R.id.layoutId);
        getWindow().setLayout((int) (width * .8), (int) (height * .55));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -100;
        getWindow().setAttributes(params);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        loadUserData();
    }

    private void loadUserData() {
        Card matchUser = getIntent().getParcelableExtra("matchUser");
        String name = matchUser.getName();
        nameTextView.setText(name);
        String matchPictureUrl;
        matchPictureUrl = matchUser.getProfileImageUrl();
        switch (matchPictureUrl) {
            case "default":
                Glide.with(getApplication()).load(R.drawable.ic_profile_hq).into(profileImage);
                break;
            default:
                Glide.with(profileImage).clear(profileImage);
                Glide.with(getApplication()).load(matchPictureUrl).into(profileImage);
                break;
        }
    }
}
