package com.pinder.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    private ImageView logo;
    private LinearLayout logoLayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    FirebaseUser user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        if (user == null) {
            Intent login = new Intent(this, LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
        } else {
            authStateListener();
            setContentView(R.layout.activity_splash);
            setObjectsById();
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.rotate);
            anim.setRepeatCount(Animation.INFINITE);
            logo.startAnimation(anim);
        }
    }

    private void authStateListener() {
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference();

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Animation animScale = AnimationUtils.loadAnimation(getApplication(), R.anim.scale);
                            logoLayout.startAnimation(animScale);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    logoLayout.setVisibility(View.GONE);
                                    logoLayout.clearAnimation();
                                    if (dataSnapshot.child("Users").child(user.getUid()).exists()) {
                                        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                            Intent intent = new Intent(SplashActivity.this, MainFragmentManager.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent requestLocationActivity = new Intent(SplashActivity.this, RequestLocationPermissionActivity.class);
                                            startActivity(requestLocationActivity);
                                            finish();
                                        }
                                    } else {
                                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }, 300);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        };
    }

    private void setObjectsById() {
        logo = findViewById(R.id.bigLogo);
        logoLayout = findViewById(R.id.logoLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null) mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (user != null) mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}