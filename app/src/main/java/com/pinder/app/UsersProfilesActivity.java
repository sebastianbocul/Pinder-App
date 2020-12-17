package com.pinder.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pinder.app.adapters.ImageAdapter;
import com.pinder.app.models.Card;
import com.pinder.app.ui.dialogs.ReportUserDialog;
import com.pinder.app.util.CalculateDistance;
import com.pinder.app.util.StringDateToAge;
import com.pinder.app.utils.BuildVariantsHelper;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;

public class UsersProfilesActivity extends AppCompatActivity {
    public String name, tags, gender, distance, location, description, profileImageUrl;
    public String userId, myId;
    ViewPager viewPager;
    DatabaseReference mImageDatabase;
    private TextView nameTextView, tagsTextView, genderTextView, distanceTextView, locationTextView, descriptionTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase, mUserProfileDatabase, myDatabaseReference;
    private ImageView dislikeButton, likeButton, reportUserButton;
    private Button unmatchButton;
    private String userAge;
    private ArrayList mImages;
    private ArrayList<String> mutualTagsExtras = null;
    private ArrayList<String> myTags = new ArrayList<>();
    private Card user;
    private ProgressBar progressBar;
    private ImageView defaultImage, backArrowImage;
    private static final String TAG = "UsersProfilesActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profiles);
        final Intent intent = getIntent();
        if (intent.getParcelableExtra("user") != null) {
            user = intent.getParcelableExtra("user");
            userId = user.getUserId();
        }
        if (intent.getStringExtra("userId") != null) {
            userId = intent.getStringExtra("userId");
        }
        nameTextView = findViewById(R.id.nameTextView);
        tagsTextView = findViewById(R.id.tagsTextView);
        genderTextView = findViewById(R.id.genderTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        locationTextView = findViewById(R.id.locationTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        unmatchButton = findViewById(R.id.unmatchButton);
        dislikeButton = findViewById(R.id.dislikeButton);
        reportUserButton = findViewById(R.id.reportUserImage);
        likeButton = findViewById(R.id.likeButton);
        progressBar = findViewById(R.id.user_progress_bar);
        defaultImage = findViewById(R.id.default_image);
        backArrowImage=findViewById(R.id.back_arrow);
        BuildVariantsHelper.disableButton(backArrowImage);
        mAuth = FirebaseAuth.getInstance();
        myId = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserProfileDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(myId).child("connections").child("matches");
        myDatabaseReference = FirebaseDatabase.getInstance().getReference();
        viewPager = findViewById(R.id.viewPager);
        mImageDatabase = mUserDatabase.child(userId).child("images");
        if (getIntent().hasExtra("userId")) {
            if (intent.getStringExtra("userId").equals(myId)) {
                unmatchButton.setVisibility(View.INVISIBLE);
                dislikeButton.setVisibility(View.INVISIBLE);
                likeButton.setVisibility(View.INVISIBLE);
                reportUserButton.setEnabled(false);
                loadTagsFirebase();
            } else {
                reportUserButton.setEnabled(true);
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UsersProfilesActivity.this, MainFragmentManager.class);
                i.putExtra("fromUsersProfilesActivity", "dislikeButtonClicked");
                startActivity(i);
            }
        });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UsersProfilesActivity.this, MainFragmentManager.class);
                i.putExtra("fromUsersProfilesActivity", "likeButtonClicked");
                startActivity(i);
            }
        });
        reportUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReportDialog();
            }
        });
        unmatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                Intent myIntent = new Intent(UsersProfilesActivity.this, MainFragmentManager.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                myIntent.setFlags();
//                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(myIntent);
                Log.d(TAG, "startActivity: ");


                unmatchButton.setText("clicked unmatch");
                myDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: "+dataSnapshot);
//                        try {
//                            String chatId = dataSnapshot.child("Users").child(myId).child("connections").child("matches").child(userId).child("ChatId").getValue().toString();
//                            //for chat
//                            DatabaseReference mRemoveChild = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);
//                            mRemoveChild.removeValue();
//                            //for me
//                            mRemoveChild = FirebaseDatabase.getInstance().getReference().child("Users").child(myId).child("connections");
//                            mRemoveChild.child("nope").child(userId).setValue(true);
//                            mRemoveChild.child("yes").child(userId).removeValue();
//                            mRemoveChild.child("matches").child(userId).removeValue();
//                            //for user
//                            mRemoveChild = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("connections");
//                            mRemoveChild.child("nope").child(myId).setValue(true);
//                            mRemoveChild.child("yes").child(myId).removeValue();
//                            mRemoveChild.child("matches").child(myId).removeValue();
//                            final Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Intent myIntent = new Intent(UsersProfilesActivity.this, MainFragmentManager.class);
//                                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(myIntent);
//                                }
//                            }, 500);
//                        } catch (NullPointerException e) {
//                            Toast.makeText(UsersProfilesActivity.this, "Unable to do that operation", Toast.LENGTH_SHORT).show();
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        backArrowImage.setOnClickListener(v->{
            onBackPressed();
        });
        if (intent.getParcelableExtra("user") == null) {
            fillUserProfileFireBase();
            loadImagesFireBase();
        } else {
            fillUserProfileBundle();
            loadImagesBundle();
        }
    }

    private void loadImagesBundle() {
        if (getIntent().getParcelableExtra("user") != null) {
            int size = user.getImages().size();
            if (user.getImages() == null || user.getImages().size()==0) {
                defaultImage.setVisibility(View.VISIBLE);
                viewPager.setBackgroundColor(Color.TRANSPARENT);
                return;
            } else if(user.getImages().size()>=1){
                defaultImage.setVisibility(View.GONE);
            }
            mImages = (ArrayList) user.getImages();
            ImageAdapter adapter = new ImageAdapter(UsersProfilesActivity.this, mImages);
            viewPager.setAdapter(adapter);
        }
    }

    private void fillUserProfileBundle() {
        if (getIntent().getParcelableExtra("user") != null) {
            distanceTextView.setText(", " + Math.round(user.getDistance()) + " km away");
            tagsTextView.setText(user.getTags().toString());
            int age = new StringDateToAge().stringDateToAge(user.getDateOfBirth());
            userAge = String.valueOf(age);
            name = user.getName();
            nameTextView.setText(name + "  " + userAge);
            gender = user.getGender();
            genderTextView.setText(gender);
            location = user.getLocation();
            locationTextView.setText(location);
            description = user.getDescription();
            descriptionTextView.setText(description);
        }
    }

    private void openReportDialog() {
        ReportUserDialog reportUserDialog = new ReportUserDialog(myId, userId);
        reportUserDialog.show(getSupportFragmentManager(), "Report User Dialog");
    }

    private void loadTagsFirebase() {
        mUserDatabase.child(myId).child("tags").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()) {
                        myTags.add(ds.getKey());
                    }
                }
                mutualTagsExtras = myTags;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadImagesFireBase() {
        progressBar.setVisibility(View.VISIBLE);
        defaultImage.setVisibility(View.GONE);
        mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    ArrayList arrayList = new ArrayList();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        arrayList.add(ds.child("uri").getValue());
                    }
                    mImages = arrayList;
                    ImageAdapter adapter = new ImageAdapter(UsersProfilesActivity.this, mImages);
                    viewPager.setAdapter(adapter);
                }else {
                    defaultImage.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void fillUserProfileFireBase() {
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
                    double distance = new CalculateDistance().distance(lat1, lon1, lat2, lon2);
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
                        int age = new StringDateToAge().stringDateToAge(map.get("dateOfBirth").toString());
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
//                        viewPager.setBackgroundColor(Color.TRANSPARENT);
//                        viewPager.setBackground(getDrawable(R.drawable.ic_profile_hq));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}
