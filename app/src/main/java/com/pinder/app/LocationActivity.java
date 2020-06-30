package com.pinder.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pinder.app.Chat.ChatActivity;
import com.pinder.app.Matches.MatchesActivity;
import com.pinder.app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {
    public String currentUID;
    Button btLocation;
    TextView textView1, textView2, textView3, textView4, textView5, name;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseDatabase database;
    DatabaseReference usersReference;
    Bitmap bitmap;
    LinearLayout linearLayout;
    private GoogleMap myGoogleMap;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDb;
    private DatabaseReference myRef;
    private ImageView profileImage, goToChat;
    private boolean toggleMapToolBarVisibility;
    private String matchId, myId;
    private boolean mapRdy = false;

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();
        final int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();
        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        linearLayout = findViewById(R.id.userLayout);
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();
        myId = mAuth.getCurrentUser().getUid();
        name = findViewById(R.id.MatchName);
        profileImage = findViewById(R.id.profileImage);
        goToChat = findViewById(R.id.goToChat);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(LocationActivity.this);
    }

    public void onMapReady(GoogleMap googleMap) {
        myGoogleMap = googleMap;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                //get my location
                mAuth = FirebaseAuth.getInstance();
                currentUID = mAuth.getCurrentUser().getUid();
                String lat;
                String lon;
                String latMap;
                String lonMap;
                if (!dataSnapshot.child("Users").child(currentUID).child("location").exists()) {
                    return;
                }
                lat = dataSnapshot.child("Users").child(currentUID).child("location").child("latitude").getValue().toString();
                lon = dataSnapshot.child("Users").child(currentUID).child("location").child("longitude").getValue().toString();
                if (getIntent().hasExtra("matchId")) {
                    latMap = dataSnapshot.child("Users").child(getIntent().getExtras().getString("matchId")).child("location").child("latitude").getValue().toString();
                    lonMap = dataSnapshot.child("Users").child(getIntent().getExtras().getString("matchId")).child("location").child("longitude").getValue().toString();
                } else {
                    latMap = lat;
                    lonMap = lon;
                }
                final double myMapLatitude = Double.parseDouble(latMap);
                final double myMapLongitude = Double.parseDouble(lonMap);
                LatLng latLngMap = new LatLng(myMapLatitude, myMapLongitude);
                final double myLatitude = Double.parseDouble(lat);
                final double myLongitude = Double.parseDouble(lon);
                LatLng latLng = new LatLng(myLatitude, myLongitude);
                if (mapRdy == false) {
                    myGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLngMap));
                    myGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngMap, 15));
                    mapRdy = true;
                    //
                }
                //set my marker at start
                MarkerOptions markerOptionsFB;
                markerOptionsFB = new MarkerOptions().position(latLng).title("I'm here").icon(BitmapDescriptorFactory.defaultMarker(300));
                Marker myMarker = myGoogleMap.addMarker(markerOptionsFB);
                myMarker.setTag(currentUID.trim());
                myGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        linearLayout.setVisibility(View.INVISIBLE);
                    }
                });
                myGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        try {
                            linearLayout.setVisibility(View.VISIBLE);
                            name.setText(dataSnapshot.child("Users").child(marker.getTag().toString()).child("name").getValue().toString());
                            String profileImageUrl = dataSnapshot.child("Users").child(marker.getTag().toString()).child("profileImageUrl").getValue().toString();
                            if (profileImageUrl.equals("default")) {
                                Glide.with(LocationActivity.this).load(R.drawable.picture_default).into(profileImage);
                            } else {
                                Glide.with(LocationActivity.this).load(dataSnapshot.child("Users").child(marker.getTag().toString()).child("profileImageUrl").getValue().toString()).into(profileImage);
                            }
                            matchId = marker.getTag().toString();
                            if (myId.equals(matchId)) {
                                if (dataSnapshot.child("Users").child(marker.getTag().toString()).child("showMyLocation").getValue().toString().equals("true")) {
                                    Glide.with(LocationActivity.this).load(R.drawable.ghost_mode).into(goToChat);
                                    goToChat.setEnabled(false);
                                } else {
                                    goToChat.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                goToChat.setVisibility(View.VISIBLE);
                                goToChat.setEnabled(true);
                                Glide.with(LocationActivity.this).load(R.drawable.matches).into(goToChat);
                            }
                            profileImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToUsersProfile();
                                }
                            });
                            goToChat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToUserChat();
                                }
                            });
                        } catch (SecurityException sec) {
                            Log.e("locationTag", sec.getMessage());
                        }
                        return false;
                    }
                });
                //matches only location
                for (final DataSnapshot ds : dataSnapshot.child("Users").child(currentUID).child("connections").child("matches").getChildren()) {
                    String userId = ds.getKey();
                    usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("showMyLocation").getValue().toString().equals("false")) {
                                return;
                            }
                            String userName;
                            if (!dataSnapshot.child("name").exists()) {
                                return;
                            }
                            userName = dataSnapshot.child("name").getValue().toString();
                            double latitude = Double.parseDouble(dataSnapshot.child("location").child("latitude").getValue().toString());
                            double longitude = Double.parseDouble(dataSnapshot.child("location").child("longitude").getValue().toString());
                            double distance = distance(myLatitude, myLongitude, latitude, longitude);
                            LatLng latLngFB = new LatLng(latitude, longitude);
                            if (dataSnapshot.child("profileImageUrl").getValue().toString().equals("default")) {
                                Log.d("locTag", "onDataChange datasnap1: " + dataSnapshot.child("profileImageUrl").getValue().toString());
                                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.map_default);
                                bitmap = bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                                Bitmap circleMarker = getCroppedBitmap(smallMarker);
                                MarkerOptions markerOptionsFB;
                                markerOptionsFB = new MarkerOptions()
                                        .position(latLngFB)
                                        .title(userName + "\r" + Math.round(distance) + "km")
                                        .icon(BitmapDescriptorFactory.fromBitmap(circleMarker));
                                Marker myMarker = myGoogleMap.addMarker(markerOptionsFB);
                                myMarker.setTag(dataSnapshot.getKey().trim());
                            } else {
                                Glide.with(LocationActivity.this)
                                        .load(dataSnapshot.child("profileImageUrl").getValue().toString())
                                        .override(100, 100)
                                        .centerCrop()
                                        .into(new CustomTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                bitmap = drawableToBitmap(resource);
                                                Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                                                Bitmap circleMarker = getCroppedBitmap(smallMarker);
                                                MarkerOptions markerOptionsFB;
                                                markerOptionsFB = new MarkerOptions()
                                                        .position(latLngFB)
                                                        .title(userName + "\r" + Math.round(distance) + "km")
                                                        .icon(BitmapDescriptorFactory.fromBitmap(circleMarker));
                                                Marker myMarker = myGoogleMap.addMarker(markerOptionsFB);
                                                myMarker.setTag(dataSnapshot.getKey().trim());
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                                // Remove the Drawable provided in onResourceReady from any Views and ensure
                                                // no references to it remain.
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 44:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void goToUsersProfile() {
        Intent intent = new Intent(LocationActivity.this, UsersProfilesActivity.class);
        intent.putExtra("userId", matchId);
        intent.putExtra("fromActivity", "LocationActivity");
        startActivity(intent);
    }

    private void goToUserChat() {
        Intent intent = new Intent(LocationActivity.this, ChatActivity.class);
        intent.putExtra("matchId", matchId);
        intent.putExtra("fromActivity", "LocationActivity");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MatchesActivity.class);
        startActivity(i);
    }

    public void onBack(View view) {
        Intent i = new Intent(this, MatchesActivity.class);
        startActivity(i);
    }
}