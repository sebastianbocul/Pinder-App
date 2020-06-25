package com.example.tinderapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.tinderapp.Images.ImageAdapter;
import com.example.tinderapp.Matches.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class UsersProfilesActivity extends AppCompatActivity {
    public String name, tags, gender, distance, location, description, phone, profileImageUrl;
    public String userId, myId, fromActivity = "empty";
    ViewPager viewPager;
    DatabaseReference mImageDatabase;
    private ImageView imageView;
    private TextView nameTextView, tagsTextView, genderTextView, distanceTextView, locationTextView, descriptionTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase, mUserProfileDatabase, myDatabaseReference;
    private ImageView dislikeButton, likeButton;
    private Button unmatchButton;
    private String userAge;
    private ArrayList imagesList, mImages;
    private ArrayList<String> mutualTagsExtras;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profiles);
        mutualTagsExtras = null;
        final Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        if (intent.getStringExtra("fromActivity") != null) {
            fromActivity = intent.getStringExtra("fromActivity");
        }
        if (intent.getStringArrayListExtra("mutualTags") != null) {
            mutualTagsExtras = intent.getStringArrayListExtra("mutualTags");
        }
        nameTextView = findViewById(R.id.nameTextView);
        tagsTextView = findViewById(R.id.tagsTextView);
        genderTextView = findViewById(R.id.genderTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        locationTextView = findViewById(R.id.locationTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        unmatchButton = findViewById(R.id.unmatchButton);
        dislikeButton = findViewById(R.id.dislikeButton);
        likeButton = findViewById(R.id.likeButton);
        mAuth = FirebaseAuth.getInstance();
        myId = mAuth.getCurrentUser().getUid();
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
                if (!dataSnapshot.exists()) return;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (userId.equals(ds.getKey())) {
                        unmatchButton.setVisibility(View.VISIBLE);
                        dislikeButton.setVisibility(View.INVISIBLE);
                        likeButton.setVisibility(View.INVISIBLE);
                    }
                }
                if (getIntent().hasExtra("userId")) {
                    if (intent.getStringExtra("userId").equals(myId)) {
                        unmatchButton.setVisibility(View.INVISIBLE);
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
                Intent i = new Intent(UsersProfilesActivity.this, MainActivity.class);
                i.putExtra("fromUsersProfilesActivity", "dislikeButtonClicked");
                startActivity(i);
            }
        });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UsersProfilesActivity.this, MainActivity.class);
                i.putExtra("fromUsersProfilesActivity", "likeButtonClicked");
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
                        } catch (NullPointerException e) {
                            Toast.makeText(UsersProfilesActivity.this, "Unable to do that operation", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        fillUserProfile();
        loadImages();
    }

    private void loadImages() {
        imagesList = new ArrayList<String>();
        mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList arrayList = new ArrayList();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    arrayList.add(ds.child("uri").getValue());
                }
                mImages = arrayList;
                ImageAdapter adapter = new ImageAdapter(UsersProfilesActivity.this, mImages);
                viewPager.setAdapter(adapter);
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void fillUserProfile() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.child(userId).getValue();
                    Map<String, Object> mapLoc = (Map<String, Object>) map.get("location");
                    Map<String, Object> mapMyLoc = (Map<String, Object>) dataSnapshot.child(myId).child("location").getValue();
                    double lat1, lon1, lat2, lon2;
                    lat1 = (double) mapMyLoc.get("latitude");
                    lon1 = (double) mapMyLoc.get("longitude");
                    lat2 = (double) mapLoc.get("latitude");
                    lon2 = (double) mapLoc.get("longitude");
                    double distance = distance(lat1, lon1, lat2, lon2);
                    distanceTextView.setText(", " + Math.round(distance) + " km away");
                    //tags
                    StringBuilder strB = new StringBuilder();
                    if (mutualTagsExtras != null) {
                        for (String str : mutualTagsExtras) {
                            strB.append("#");
                            strB.append(str + " ");
                        }
                    } else if (dataSnapshot.child(myId).child("connections").child("matches").child(userId).child("mutualTags").exists()) {
                        Map<String, Object> tags = (Map<String, Object>) dataSnapshot.child(myId).child("connections").child("matches").child(userId).child("mutualTags").getValue();
                        ArrayList<String> stringList = new ArrayList(tags.keySet());
                        for (String str : stringList) {
                            strB.append("#");
                            strB.append(str + " ");
                        }
                    }
                    if (strB.length() != 0) {
                        tagsTextView.setText(strB.toString());
                    } else {
                        tagsTextView.setText(" ");
                    }
                    userAge = "";
                    if (map.get("dateOfBirth") != null) {
                        int age = stringDateToAge(map.get("dateOfBirth").toString());
                        userAge = String.valueOf(age);
                    }
                    //name
                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        nameTextView.setText(name + "  " + userAge);
                    } else descriptionTextView.setText("Name: ");
                    //sex
                    if (map.get("sex") != null) {
                        gender = map.get("sex").toString();
                        genderTextView.setText(gender);
                    } else descriptionTextView.setText("Gender:");
                    //location
                    if (mapLoc.get("locality") != null) {
                        location = mapLoc.get("locality").toString();
                        locationTextView.setText(location);
                    } else locationTextView.setText("");
                    //description
                    if (map.get("description") != null) {
                        description = map.get("description").toString();
                        descriptionTextView.setText(description);
                    } else descriptionTextView.setText("");
                    if (map.get("images") == null) {
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
        dist = dist * 60 * 1.1515 * 1.609344;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public int stringDateToAge(String strDate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return stringDateToAgeOreo(strDate);
        } else {
            return stringDateToAgeOld(strDate);
        }
    }

    public int stringDateToAgeOld(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar dob = Calendar.getInstance();
        try {
            dob.setTime(sdf.parse(strDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar today = Calendar.getInstance();
        int curYear = today.get(Calendar.YEAR);
        int dobYear = dob.get(Calendar.YEAR);
        int age = curYear - dobYear;
        // if dob is month or day is behind today's month or day
        // reduce age by 1
        int curMonth = today.get(Calendar.MONTH);
        int dobMonth = dob.get(Calendar.MONTH);
        if (dobMonth > curMonth) { // this year can't be counted!
            age--;
        } else if (dobMonth == curMonth) { // same month? check for day
            int curDay = today.get(Calendar.DAY_OF_MONTH);
            int dobDay = dob.get(Calendar.DAY_OF_MONTH);
            if (dobDay > curDay) { // this year can't be counted!
                age--;
            }
        }
        return age;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int stringDateToAgeOreo(String strDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        LocalDate date = LocalDate.parse(strDate, formatter);
        Date c = Calendar.getInstance().getTime();
        LocalDate today = LocalDate.now();
        return calculateAge(date, today);
    }
}
