package com.example.tinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class UsersProfilesActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView nameTextView,tagsTextView,genderTextView,distanceTextView,locationTextView,descriptionTextView;
    public String name,tags,gender,distance,location,description,phone,profileImageUrl;
    public String userId,myId;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profiles);

        Intent intent = getIntent();
         userId=intent.getStringExtra("userId");

        imageView = (ImageView) findViewById(R.id.imageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        tagsTextView = (TextView) findViewById(R.id.tagsTextView);
        genderTextView= (TextView) findViewById(R.id.genderTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);


        mAuth =  FirebaseAuth.getInstance();
        myId= mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        fillUserProfile();

    }

public void fillUserProfile(){
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.child(userId).getValue();
                    Map<String, Object> mapLoc = (Map<String, Object>) map.get("location");
                    Map<String, Object> mapMyLoc = (Map<String, Object>) dataSnapshot.child(myId).child("location").getValue();

                    double lat1,lon1,lat2,lon2;

                    lat1= (double) mapMyLoc.get("latitude");
                    lon1= (double) mapMyLoc.get("longitude");
                    lat2= (double) mapLoc.get("latitude");
                    lon2= (double) mapLoc.get("longitude");


                    double distance = distance(lat1,lon1,lat2,lon2);
                    distanceTextView.setText("Distance: " + Math.round(distance) + " km");

                    //name
                    if(map.get("name")!=null){
                        name = map.get("name").toString();
                        nameTextView.setText("Name: "+name);
                    }else descriptionTextView.setText("Name: ");

                    //tags
                    tagsTextView.setText("Tags: ");


                    //sex
                    if(map.get("sex")!=null){
                        gender = map.get("sex").toString();
                        genderTextView.setText("Gender: " +gender);
                    }else descriptionTextView.setText("Gender:");

                    //location
                    if(mapLoc.get("locality")!=null){
                        location = mapLoc.get("locality").toString();
                        locationTextView.setText("Location: " + location);
                    }else locationTextView.setText("Location:");

                    //description
                    if(map.get("description")!=null){
                        description = map.get("description").toString();
                        descriptionTextView.setText("Description: " +description);
                    }else descriptionTextView.setText("Description:");

                    //image
                    Glide.with(imageView).clear(imageView);
                    if(map.get("profileImageUrl")!=null){
                        profileImageUrl = map.get("profileImageUrl").toString();

                        switch (profileImageUrl) {

                            case "default":
                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(imageView);
                                break;

                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(imageView);
                                break;


                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




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
        dist = dist * 60 * 1.1515* 1.609344;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
