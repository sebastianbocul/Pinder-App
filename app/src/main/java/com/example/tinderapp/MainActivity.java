package com.example.tinderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tinderapp.Cards.arrayAdapter;
import com.example.tinderapp.Cards.cards;
import com.example.tinderapp.Matches.MatchesActivity;
import com.example.tinderapp.Tags.TagsAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private cards cards_data;
    private com.example.tinderapp.Cards.arrayAdapter arrayAdapter;
    private int i;


    public Button likeButton,dislikeButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth mAuth;
    private TextView tags;
    private DatabaseReference usersDb;
    public String currentUID;
    public TextView noMoreEditText;
    public SwipeFlingAdapterView flingContainer;
    ListView listView;
    List<cards> rowItems;
    TagsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        checkUserSex();
        noMoreEditText = (TextView) findViewById(R.id.noMore);
        rowItems = new ArrayList<cards>();
        arrayAdapter = new arrayAdapter(this, R.layout.item,rowItems );
        likeButton = (Button) findViewById(R.id.likeButton);
        dislikeButton = (Button) findViewById(R.id.dislikeButton);
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //check location permission
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            updateLocation();
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this, new  String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }


        arrayAdapter = new arrayAdapter(this, R.layout.item,rowItems );
        flingContainer.setAdapter(arrayAdapter);


        fillTagsAdapter();




        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flingContainer.getChildCount()!=0)
                    flingContainer.getTopCardListener().selectRight();
                else Toast.makeText(MainActivity.this,"There is no more users",Toast.LENGTH_SHORT).show();

            }
        });

        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flingContainer.getChildCount()!=0)
                flingContainer.getTopCardListener().selectLeft();
                else Toast.makeText(MainActivity.this,"There is no more users",Toast.LENGTH_SHORT).show();
            }
        });



        //created delay so flingContainer is loaded - coudnt find other solutin
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                if(flingContainer!=null){
                    if(intent.getStringExtra("fromUsersProfilesActivity")!=null){
                        String s = intent.getStringExtra("fromUsersProfilesActivity");
                        if(s.equals("likeButtonClicked")){
                            if(flingContainer.getChildCount()!=0)
                                flingContainer.getTopCardListener().selectRight();
                            else Toast.makeText(MainActivity.this,"There is no more users",Toast.LENGTH_SHORT).show();
                        }
                        if(s.equals("dislikeButtonClicked")){
                            if(flingContainer.getChildCount()!=0)
                                flingContainer.getTopCardListener().selectLeft();
                            else Toast.makeText(MainActivity.this,"There is no more users",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        }, 300);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {

            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUID).setValue(true);
                Toast.makeText(MainActivity.this,"left",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yes").child(currentUID).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this,"right",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                /*al.add("XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;*/
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                Toast.makeText(MainActivity.this,"click",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,UsersProfilesActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);

            }
        });

    }


    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUID).child("connections").child("yes").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this, "new Connection", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUID).child("ChatId").setValue(key);
                    usersDb.child(currentUID).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                    String matchId = dataSnapshot.getKey();
                    //popactivity when matched
                    Intent i = new Intent(getApplicationContext(),PopActivity.class);
                    i.putExtra("matchId",matchId);
                    startActivity(i);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String userSex;
    private String oppositeUserSex;
    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.getKey().equals(user.getUid())){
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("sex").getValue()!=null){
                        userSex=dataSnapshot.child("sex").getValue().toString();
                        switch (userSex){
                            case "Male":
                                oppositeUserSex="Female";
                                break;
                            case "Female":
                                oppositeUserSex="Male";
                                break;
                             }
                              getOppositeSexUsers();
                         }
                    else {
                        noMoreEditText.setText("There is no more users");
                    }
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void getOppositeSexUsers(){
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.child("sex").getValue()!=null){
                if(dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUID) && !dataSnapshot.child("connections").child("yes").hasChild(currentUID)&& dataSnapshot.child("sex").getValue().toString().equals(oppositeUserSex)){
                    String profileImageUrl = "default";
                    if(!dataSnapshot.child("profileImageUrl").getValue().toString().equals("default")){
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }
                    cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl,"Tags");
                    rowItems.add(item);
                    arrayAdapter.notifyDataSetChanged();

                }
                }
                noMoreEditText.setText("There is no more users");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void goToProfile(View view) {

        checkUserSex();
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra("userSex", userSex);
        startActivity(intent);
        return;
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        return;
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
        return;
    }



    public void updateLocation(){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location!=null){
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);


                        DatabaseReference myRef = usersDb.child(currentUID).child("location");

                        myRef.child("longitude").setValue(addresses.get(0).getLongitude());
                        myRef.child("latitude").setValue(addresses.get(0).getLatitude());

                        if(addresses.get(0).getCountryName()!=null){
                            myRef.child("countryName").setValue(addresses.get(0).getCountryName());
                        }else {
                            myRef.child("countryName").setValue("Not found");
                        }

                        if(addresses.get(0).getLocality()!=null){
                            myRef.child("locality").setValue(addresses.get(0).getLocality());
                        }else {
                            myRef.child("locality").setValue("Not found");
                        }

                        if(addresses.get(0).getAddressLine(0)!=null){
                            myRef.child("address").setValue(addresses.get(0).getAddressLine(0));
                        }else {
                            myRef.child("address").setValue("Not found");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }


    private void fillTagsAdapter() {
        //  super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        // data to populate the RecyclerView with
        ArrayList<String> myTags = new ArrayList<>();
        myTags.add("Date");
        myTags.add("go_out");
        myTags.add("party");
        myTags.add("grill");
        myTags.add("beer");
        myTags.add("warzone");
        myTags.add("League_of_legends");
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.tagsRecyclerView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new TagsAdapter(this, myTags);
        //    adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


}
