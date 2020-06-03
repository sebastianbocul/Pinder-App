package com.example.tinderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tinderapp.Cards.arrayAdapter;
import com.example.tinderapp.Cards.cards;
import com.example.tinderapp.Matches.MatchesActivity;
import com.example.tinderapp.Notifications.APIService;
import com.example.tinderapp.Notifications.Client;
import com.example.tinderapp.Notifications.Data;
import com.example.tinderapp.Notifications.Sender;
import com.example.tinderapp.Notifications.Token;
import com.example.tinderapp.Tags.TagsAdapter;
import com.example.tinderapp.Tags.TagsManagerActivity;
import com.example.tinderapp.Tags.TagsManagerObject;
import com.example.tinderapp.Tags.TagsPopularObject;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private cards cards_data;
    private com.example.tinderapp.Cards.arrayAdapter arrayAdapter;
    private int i;


    public ImageView likeButton,dislikeButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth mAuth;
    private TextView tags;
    private DatabaseReference usersDb;
    public String currentUID;
    public TextView noMoreEditText;
    public SwipeFlingAdapterView flingContainer;
    ListView listView;
    List<cards> rowItems;
    private TagsAdapter adapter;
    private ArrayList<TagsManagerObject> myTagsList = new ArrayList<>();
    private double myLatitude,myLongitude;
    private Map<String, String> myInfo = new HashMap<>();
    String mUID;
    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        noMoreEditText = (TextView) findViewById(R.id.noMore);
        rowItems = new ArrayList<cards>();
        arrayAdapter = new arrayAdapter(this, R.layout.item,rowItems );
        likeButton = findViewById(R.id.likeButton);
        dislikeButton = findViewById(R.id.dislikeButton);
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

        //create APISERVICE
        Client client = new Client();
        apiService = client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        //Client.
        //   api= client.get
        //  getUsersFromDb();
        checkUserStatus();
        //update token
        updateToken(FirebaseInstanceId.getInstance().getToken());




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
                isConnectionMatch(userId,obj);
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
                cards objec = (cards) dataObject;
                String userId = objec.getUserId();
                Toast.makeText(MainActivity.this,"click",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this,UsersProfilesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("userId", userId);
                Log.d("mainActivity","Number of cards : " + rowItems);
                ArrayList<String> stringList = new ArrayList(objec.getMutualTagsMap().keySet());
                Log.d("main",stringList.toString());
                intent.putStringArrayListExtra("mutualTags",  stringList);
                startActivity(intent);

            }
        });

    }


    private void isConnectionMatch(final String userId,cards obj) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUID).child("connections").child("yes").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this, "new Connection", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUID).child("ChatId").setValue(key);
                    usersDb.child(currentUID).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUID).child("mutualTags").updateChildren(obj.getMutualTagsMap());
                    usersDb.child(currentUID).child("connections").child("matches").child(dataSnapshot.getKey()).child("mutualTags").updateChildren(obj.getMutualTagsMap());
                    String matchId = dataSnapshot.getKey();

                    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String myName=currentUID;
                            if(notify){
                                sendNotification(matchId,myName," ");
                            }
                            notify=false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //notify
                    notify=true;

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
//
//    private String userSex;
//    private String oppositeUserSex;
//    public void checkUserSex(){
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference userDb = usersDb.child(user.getUid());
//        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//            if(dataSnapshot.getKey().equals(user.getUid())){
//                if(dataSnapshot.exists()){
//                    if(dataSnapshot.child("sex").getValue()!=null){
//                        userSex=dataSnapshot.child("sex").getValue().toString();
//                        switch (userSex){
//                            case "Male":
//                                oppositeUserSex="Female";
//                                break;
//                            case "Female":
//                                oppositeUserSex="Male";
//                                break;
//                             }
//                              getOppositeSexUsers();
//                         }
//                    else {
//                        noMoreEditText.setText("There is no more users");
//                    }
//                    }
//
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//
//    public void getOppositeSexUsers(){
//        usersDb.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(dataSnapshot.child("sex").getValue()!=null){
//
//                    if(dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUID) && !dataSnapshot.child("connections").child("yes").hasChild(currentUID) && dataSnapshot.child("sex").getValue().toString().equals(oppositeUserSex)){
//                        getTagsPreferencesUsers(dataSnapshot);
//                    }
//                }
//                noMoreEditText.setText("There is no more users");
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//


    public void goToProfile(View view) {

        //     checkUserSex();
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        //     intent.putExtra("userSex", userSex);
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

                        myLongitude=addresses.get(0).getLongitude();
                        myLatitude= addresses.get(0).getLatitude();
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
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID).child("tags");
        ArrayList<String> myTags = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.tagsRecyclerView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        myTags.add("#"+ ds.getKey());
                        //  myTagsList.add(ds.getKey());
                        String tagName = ds.getKey().toLowerCase();
                        String gender = ds.child("gender").getValue().toString();
                        String mAgeMax = ds.child("maxAge").getValue().toString();
                        String mAgeMin = ds.child("minAge").getValue().toString();
                        String mDistance = ds.child("maxDistance").getValue().toString();
                        TagsManagerObject obj = new TagsManagerObject(tagName,gender,mAgeMin,mAgeMax,mDistance);
                        myTagsList.add(obj);


                    }

                    adapter = new TagsAdapter(MainActivity.this, myTags);
                    recyclerView.setAdapter(adapter);

                }
                else {
                    myTags.add("Add tags in options first!");
                    adapter = new TagsAdapter(MainActivity.this, myTags);
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
    public void onBack(View view) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    private ArrayList<String> first;
    private void getUsersFromDb(){
        first = new ArrayList<>();
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference newUserDb = FirebaseDatabase.getInstance().getReference().child("Users");
        String newCurrentUID =FirebaseAuth.getInstance().getCurrentUser().getUid();



        newUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("maingetTag", "datasnapshot " + dataSnapshot);
                if(dataSnapshot.child(currentUID).child("sex").exists()){
                    Log.d("maingetTag", "myInfo.put sex ");
                    myInfo.put("sex",dataSnapshot.child(currentUID).child("sex").getValue().toString());
                }
                if(dataSnapshot.child(currentUID).child("dateOfBirth").exists()){
                    int myAge = stringDateToAge(dataSnapshot.child(currentUID).child("dateOfBirth").getValue().toString());
                    Log.d("maingetTag", "myInfo.put myage ");
                    myInfo.put("age", String.valueOf(myAge));
                }

                if(dataSnapshot.child(currentUID).child("connections").child("yes").exists()){
                    for (DataSnapshot ds: dataSnapshot.child(currentUID).child("connections").child("yes").getChildren()){
                        if(!dataSnapshot.child(currentUID).child("connections").child("matches").hasChild(ds.getKey())){
                            Log.d("first", "onDataChange: " + ds.getKey());
                            getTagsPreferencesUsers(dataSnapshot.child(ds.getKey()));
                            first.add(ds.getKey());
                        }
                    }
                }

                Log.d("first", "after loop: " );

                newUserDb.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.child("sex").getValue()!=null){

                            if(dataSnapshot.exists() && !first.contains(dataSnapshot.getKey()) && !dataSnapshot.child("connections").child("nope").hasChild(currentUID) && !dataSnapshot.child("connections").child("yes").hasChild(currentUID) &&!dataSnapshot.getKey().equals(newCurrentUID)){
                                dataSnapshot.getKey().equals(currentUID);
                                Log.d("first", "OnChillAdded: " + dataSnapshot.getKey());


                                getTagsPreferencesUsers(dataSnapshot);
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void getTagsPreferencesUsers(DataSnapshot ds) {
        ArrayList<String> mutalTagsList = new ArrayList<>();
        StringBuilder mutalTagsSB=new StringBuilder();
        Map tagsMap = new HashMap<>();
        Log.d("maingetTag", "User Name " + ds.child("name").getValue().toString());
        try {
            int age = stringDateToAge(ds.child("dateOfBirth").getValue().toString());
            int myAge = Integer.parseInt(myInfo.get("age"));
            Log.d("maingetTag", "try ");
            double latitude = Double.parseDouble(ds.child("location").child("latitude").getValue().toString());
            double longitude = Double.parseDouble(ds.child("location").child("longitude").getValue().toString());
            double distanceDouble = distance(myLatitude, myLongitude, latitude, longitude);
            for (DataSnapshot dataTag : ds.child("tags").getChildren()) {
                Log.d("maingetTag", "forFirst ");
                for (TagsManagerObject tag : myTagsList) {
                    ///VALIDATING MY PREFERENCES
                    //comparing tags
                    Log.d("maingetTag", "1st if: " +dataTag.getKey() + " == " +tag.getTagName());
                    if (dataTag.getKey().toString().equals(tag.getTagName())) {
                        //validating my gender preferences
                        Log.d("maingetTag", "2nd if: " + tag.getGender() + " == " +ds.child("sex").getValue().toString() + "  ||  " + tag.getGender() + " == Any");
                        if (tag.getGender().equals(ds.child("sex").getValue().toString()) || tag.getGender().equals("Any")) {
                            Log.d("maingetTag", "3rd if: " + dataTag.child("gender").getValue().toString() + " == " + myInfo.get("sex") + "  ||  " +  dataTag.child("gender").getValue().toString() + " == Any");
                            //validating user gender preferences
                            if(dataTag.child("gender").getValue().toString().equals(myInfo.get("sex")) || dataTag.child("gender").getValue().toString().equals("Any")){
                                Log.d("maingetTag", "4th if: " + tag.getmAgeMin() + " <= " +age + "  &&  " + tag.getmAgeMax() + " >= " +age);
                                //validating myTag age preferences with minAge and maxAge
                            if (Integer.parseInt(tag.getmAgeMin()) <= age && Integer.parseInt(tag.getmAgeMax()) >= age) {
                                Log.d("maingetTag", "5th if: " + dataTag.child("minAge").getValue().toString() + " <= " + myAge + "  &&  " + dataTag.child("maxAge").getValue().toString()+ " >= " + myAge);
                                //validating userTag age preferences with minAge and maxAge
                                if(Integer.parseInt(dataTag.child("minAge").getValue().toString()) <= myAge && Integer.parseInt(dataTag.child("maxAge").getValue().toString()) >=myAge) {
                                    Log.d("maingetTag", "6th if: " + tag.getmDistance() + " >= " +distanceDouble);
                                    //validating myTag distance preference
                                    if (Double.parseDouble(tag.getmDistance()) >= distanceDouble) {
                                        //validate userTag distance preference
                                        Log.d("maingetTag", "7th if: " + dataTag.child("maxDistance").getValue().toString()+ " >= " +distanceDouble);
                                        if(Double.parseDouble(dataTag.child("maxDistance").getValue().toString())>=distanceDouble){
                                        ///CAN VALIDATE OTHER USER PREFERENCES
                                        mutalTagsList.add(tag.getTagName());
                                        tagsMap.put(tag.getTagName(), true);
                                        mutalTagsSB.append("#" + tag.getTagName() + " ");
                                        }
                                    }
                                }

                              }

                             }
                        }
                    }
                }
            }
            if (mutalTagsList.size() != 0) {
                String profileImageUrl = "default";
                if (ds.child("profileImageUrl").exists()) {
                    if (!ds.child("profileImageUrl").getValue().toString().equals("default")) {
                        profileImageUrl = ds.child("profileImageUrl").getValue().toString();
                    }
                } else profileImageUrl = "default";
                cards item = new cards(ds.getKey(), ds.child("name").getValue().toString(), profileImageUrl, mutalTagsSB.toString(), tagsMap, distanceDouble);
                rowItems.add(item);
               // sortCollection();
                for(cards card:rowItems){
                    Log.d("maingetTag", "User: " + card.getName() + "   UserId: " + card.getUserId() +   "   distance: " + card.getDistance());
                }
                arrayAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
        }
    }

    private void sortCollection() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            Collections.sort(rowItems, Comparator.comparing(cards::getDistance));
        }
        else {
            Collections.sort(rowItems, new Comparator<cards>(){
                public int compare(cards o1, cards o2){
                    if(o1.getDistance() == o2.getDistance())
                        return 0;
                    return o1.getDistance() < o2.getDistance() ? -1 : 1;
                }
            });
        }
    }


    public void goToTags(View view) {
        Intent intent = new Intent(MainActivity.this, TagsManagerActivity.class);
        startActivity(intent);
        return;
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateMyLoc();
        fillTagsAdapter();
        if(rowItems.size()==0){
            getUsersFromDb();
        }
    }
    private void updateMyLoc(){
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID).child("location");
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    myLatitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                    myLongitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public int stringDateToAge(String strDate){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
           return stringDateToAgeOreo(strDate);
        }
        else {
            return stringDateToAgeOld(strDate);
        }
    }

    public int stringDateToAgeOld(String strDate){
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
    public int stringDateToAgeOreo(String strDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        LocalDate date = LocalDate.parse(strDate, formatter);
        Date c = Calendar.getInstance().getTime();
        LocalDate today = LocalDate.now();

        return calculateAge(date,today);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {

        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
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
        dist = dist * 60 * 1.1515* 1.609344;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    private void checkUserStatus(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            mUID = user.getUid();
            //save UID of current signin user in shared preferences
            SharedPreferences sp =getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID",mUID);
            editor.apply();
        }
    }

    private void sendNotification(String matchId, String myName, String sendMessageText) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(matchId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(currentUID,R.drawable.login_photo,"Check out now!","New match!",matchId);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            //       Toast.makeText(ChatActivity.this,""+response.message(),Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

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
