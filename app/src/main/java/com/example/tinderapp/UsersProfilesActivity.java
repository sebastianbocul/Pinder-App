package com.example.tinderapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tinderapp.Images.ImageAdapter;
import com.example.tinderapp.Matches.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class UsersProfilesActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView nameTextView,tagsTextView,genderTextView,distanceTextView,locationTextView,descriptionTextView;
    public String name,tags,gender,distance,location,description,phone,profileImageUrl;
    public String userId,myId,fromActivity="empty";

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase, mUserProfileDatabase,myDatabaseReference;
    private ImageView dislikeButton,likeButton;
    private Button unmatchButton;
    ViewPager viewPager;
    private ArrayList imagesList,mImages;
    DatabaseReference mImageDatabase;
    private ArrayList<String> mutualTagsExtras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profiles);
        mutualTagsExtras=null;
        final Intent intent = getIntent();
         userId=intent.getStringExtra("userId");
         if(intent.getStringExtra("fromActivity")!=null){
             fromActivity = intent.getStringExtra("fromActivity");
         }
         if(intent.getStringArrayListExtra("mutualTags")!=null){
             mutualTagsExtras = intent.getStringArrayListExtra("mutualTags");
         }

     //   imageView = (ImageView) findViewById(R.id.imageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        tagsTextView = (TextView) findViewById(R.id.tagsTextView);
        genderTextView= (TextView) findViewById(R.id.genderTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        unmatchButton = (Button) findViewById(R.id.unmatchButton);
        dislikeButton = findViewById(R.id.dislikeButton);
        likeButton= findViewById(R.id.likeButton);
        mAuth =  FirebaseAuth.getInstance();
        myId= mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserProfileDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(myId).child("connections").child("matches");
        myDatabaseReference = FirebaseDatabase.getInstance().getReference();
        viewPager = findViewById(R.id.viewPager);
        mImageDatabase = mUserDatabase.child(userId).child("images");


        mUserProfileDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                unmatchButton.setVisibility(View.INVISIBLE);
                dislikeButton.setVisibility(View.VISIBLE);
                likeButton.setVisibility(View.VISIBLE);
                if(!dataSnapshot.exists()) return;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(userId.equals(ds.getKey())){
                        unmatchButton.setVisibility(View.VISIBLE);
                        dislikeButton.setVisibility(View.INVISIBLE);
                        likeButton.setVisibility(View.INVISIBLE);

                    }
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dislikeButton.setText("Clicked Dislike");
                Intent i = new Intent(UsersProfilesActivity.this,MainActivity.class);
                i.putExtra("fromUsersProfilesActivity","dislikeButtonClicked");
                startActivity(i);
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //likeButton.setText("Clicked like");
                Intent i = new Intent(UsersProfilesActivity.this,MainActivity.class);
                i.putExtra("fromUsersProfilesActivity","likeButtonClicked");
                startActivity(i);
            }
        });

        unmatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unmatchButton.setText("clicked unmatch");

                myDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //myId, userId
                        //need to remove chat, move users from 'matched' to nopes
                        //
                        try {
                            String chatId;
                            chatId = dataSnapshot.child("Users").child(myId).child("connections").child("matches").child(userId).child("ChatId").getValue().toString();
                            //for chat
                            DatabaseReference mRemoveChild = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);
                            mRemoveChild.removeValue();
                            //for me
                            mRemoveChild = FirebaseDatabase.getInstance().getReference().child("Users").child(myId).child("connections");
                            mRemoveChild.child("nope").child(userId).setValue(true);
                            mRemoveChild.child("yes").child(userId).removeValue();
                            mRemoveChild.child("matches").child(userId).removeValue();
                            //for user
                            mRemoveChild = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("connections");
                            mRemoveChild.child("nope").child(myId).setValue(true);
                            mRemoveChild.child("yes").child(myId).removeValue();
                            mRemoveChild.child("matches").child(myId).removeValue();


                            /////
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(UsersProfilesActivity.this, MatchesActivity.class);
                                    startActivity(intent);
                                }
                            }, 500);
                        }catch (NullPointerException e){
                            Toast.makeText(UsersProfilesActivity.this,"Unable to do that operation",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //again need some time to get response from database to update users in MatchesActivity


            }

        });
        //method nr1/
        /*
        if(fromActivity.equals("ChatActivity")){

            unmatchButton.setEnabled(true);
        }else unmatchButton.setEnabled(false);*/
        fillUserProfile();
        loadImages();
    }



    private void loadImages() {
        imagesList = new ArrayList<String>();

        mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList arrayList = new ArrayList();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    arrayList.add(ds.child("uri").getValue());
                }
                mImages=arrayList;
                ImageAdapter adapter = new ImageAdapter(UsersProfilesActivity.this,mImages);
                viewPager.setAdapter(adapter);
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

public void fillUserProfile(){
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                    //tags
                    StringBuilder strB = new StringBuilder();
                    if(mutualTagsExtras!=null){
                        for(String str: mutualTagsExtras){
                            strB.append("#");
                            strB.append(str + " ");
                        }
                    }else if(dataSnapshot.child(myId).child("connections").child("matches").child(userId).child("mutualTags").exists()){
                        Map<String, Object> tags = (Map<String, Object>) dataSnapshot.child(myId).child("connections").child("matches").child(userId).child("mutualTags").getValue();
                        ArrayList<String> stringList = new ArrayList(tags.keySet());
                        for(String str: stringList){
                            strB.append("#");
                            strB.append(str + " ");
                        }
                    }
                    if(strB.length()!=0){
                        tagsTextView.setText("Mutual tags: " + strB.toString());
                    }else {
                        tagsTextView.setText("Mutual tags: ");
                    }

                    //name
                    if(map.get("name")!=null){
                        name = map.get("name").toString();
                        nameTextView.setText("Name: "+name);
                    }else descriptionTextView.setText("Name: ");



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



                    //dataSnapshot.child("images").exists()
                    if(map.get("images")==null){
                        viewPager.setBackground(getDrawable(R.drawable.picture_default));
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
