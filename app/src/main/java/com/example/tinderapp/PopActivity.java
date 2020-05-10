package com.example.tinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PopActivity extends AppCompatActivity {


    private ImageView profileImage;
    private DatabaseReference db;
    private TextView textView;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        String matchId = getIntent().getExtras().getString("matchId");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        textView = (TextView) findViewById(R.id.topTextView);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        linearLayout=(LinearLayout) findViewById(R.id.layoutId);
        getWindow().setLayout((int)(width*.8),(int)(height*.55));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x = 0;
        params.y=0;
        getWindow().setAttributes(params);


        db = FirebaseDatabase.getInstance().getReference().child("Users").child(matchId);


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
               // Intent i = new Intent(getApplicationContext(),MainActivity.class);
           //  startActivity(i);
            }
        });

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String matchPictureUrl = dataSnapshot.child("profileImageUrl").getValue().toString();

                switch(matchPictureUrl){
                    case "default":
                        Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(profileImage);
                        break;
                    default:
                        Glide.with(profileImage).clear(profileImage);
                        Glide.with(getApplication()).load(matchPictureUrl).into(profileImage);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
