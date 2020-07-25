package com.pinder.app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.pinder.app.Cards.arrayAdapter;
import com.pinder.app.Cards.cards;
import com.pinder.app.MyFunctions.CalculateDistance;
import com.pinder.app.MyFunctions.StringDateToAge;
import com.pinder.app.Notifications.APIService;
import com.pinder.app.Notifications.Client;
import com.pinder.app.Notifications.Data;
import com.pinder.app.Notifications.Sender;
import com.pinder.app.Notifications.Token;
import com.pinder.app.Tags.TagsManagerAdapter;
import com.pinder.app.Tags.MainTags.TagsObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView likeButton, dislikeButton;
    private String currentUID;
    private TextView noMoreEditText;
    private SwipeFlingAdapterView flingContainer;
    List<cards> rowItems;
    List<cards> rowItemsRxJava;
    String mUID;
    APIService apiService;
    boolean notify = false;
    private com.pinder.app.Cards.arrayAdapter arrayAdapter;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDb;
    private TagsManagerAdapter adapter;
    private ArrayList<TagsObject> myTagsList = new ArrayList<>();
    private double myLatitude, myLongitude;
    private Map<String, String> myInfo = new HashMap<>();
    private String sortByDistance = "true";
    private ArrayList<String> first;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        try {
            currentUID = mAuth.getCurrentUser().getUid();
        } catch (Exception e) {
        }
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        noMoreEditText = getView().findViewById(R.id.noMore);
        rowItems = new ArrayList<cards>();
        rowItemsRxJava = new ArrayList<cards>();
        arrayAdapter = new arrayAdapter(getContext(), R.layout.item, rowItems);
        likeButton = getView().findViewById(R.id.likeButton);
        dislikeButton = getView().findViewById(R.id.dislikeButton);
        flingContainer = getView().findViewById(R.id.frame);
        Log.d("mainfragmentfunctions", "onViewCreated: " + rowItems.size());
        ProfileFragment profileFragment = new ProfileFragment();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        //check location permission
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // updateLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        flingContainer.setAdapter(arrayAdapter);
        //create APISERVICE
        Client client = new Client();
        apiService = client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        checkUserStatus();
        //update token
        updateToken(FirebaseInstanceId.getInstance().getToken());
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flingContainer.getChildCount() != 0)
                    flingContainer.getTopCardListener().selectRight();
                else
                    Toast.makeText(getContext(), "There is no more users", Toast.LENGTH_SHORT).show();
            }
        });
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flingContainer.getChildCount() != 0)
                    flingContainer.getTopCardListener().selectLeft();
                else
                    Toast.makeText(getContext(), "There is no more users", Toast.LENGTH_SHORT).show();
            }
        });
        swipeIfButtonClickedInUserProfile();
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
                Toast.makeText(getContext(), "Disliked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yes").child(currentUID).setValue(true);
                isConnectionMatch(userId, obj);
                Toast.makeText(getContext(), "Liked!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), UsersProfilesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("userId", userId);
                Log.d("mainActivity", "Number of cards : " + rowItems);
                ArrayList<String> stringList = new ArrayList(objec.getMutualTagsMap().keySet());
                Log.d("main", stringList.toString());
                intent.putStringArrayListExtra("mutualTags", stringList);
                startActivity(intent);
            }
        });
    }

    private void swipeIfButtonClickedInUserProfile() {
        //created delay so flingContainer is loaded - coudnt find other solutin
        //while we click like/dislike in userprofile we need wait to load cards so swipe left/right will run
        //perhaps if we switch from activities to fragments we wont need to load data and delay will be useless
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                Intent intent = getActivity().getIntent();
                if (flingContainer != null) {
                    if (intent.getStringExtra("fromUsersProfilesActivity") != null) {
                        String s = intent.getStringExtra("fromUsersProfilesActivity");
                        if (s.equals("likeButtonClicked")) {
                            if (flingContainer.getChildCount() != 0)
                                flingContainer.getTopCardListener().selectRight();
                            else
                                Toast.makeText(getContext(), "There is no more users", Toast.LENGTH_SHORT).show();
                        }
                        if (s.equals("dislikeButtonClicked")) {
                            if (flingContainer.getChildCount() != 0)
                                flingContainer.getTopCardListener().selectLeft();
                            else
                                Toast.makeText(getContext(), "There is no more users", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }, 300);
    }

    private void isConnectionMatch(final String userId, cards obj) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUID).child("connections").child("yes").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getContext(), "New connection!", Toast.LENGTH_LONG).show();
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
                            String myName = currentUID;
                            if (notify) {
                                sendNotification(matchId, myName, " ");
                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    notify = true;
                    //popactivity when matched
                    Intent i = new Intent(getActivity().getApplicationContext(), PopActivity.class);
                    i.putExtra("matchId", matchId);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void updateLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        myLongitude = addresses.get(0).getLongitude();
                        myLatitude = addresses.get(0).getLatitude();
                        DatabaseReference myRef = usersDb.child(currentUID).child("location");
                        myRef.child("longitude").setValue(addresses.get(0).getLongitude());
                        myRef.child("latitude").setValue(addresses.get(0).getLatitude());
                        if (addresses.get(0).getCountryName() != null) {
                            myRef.child("countryName").setValue(addresses.get(0).getCountryName());
                        } else {
                            myRef.child("countryName").setValue("Not found");
                        }
                        if (addresses.get(0).getLocality() != null) {
                            myRef.child("locality").setValue(addresses.get(0).getLocality());
                        } else {
                            myRef.child("locality").setValue("Not found");
                        }
                        if (addresses.get(0).getAddressLine(0) != null) {
                            myRef.child("address").setValue(addresses.get(0).getAddressLine(0));
                        } else {
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
        RecyclerView recyclerView = getView().findViewById(R.id.tagsRecyclerView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        Log.d("mainfragmentfunctions", "fillTagsAdapter");
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        myTags.add("#" + ds.getKey());
                        String tagName = ds.getKey().toLowerCase();
                        String gender = ds.child("gender").getValue().toString();
                        String mAgeMax = ds.child("maxAge").getValue().toString();
                        String mAgeMin = ds.child("minAge").getValue().toString();
                        String mDistance = ds.child("maxDistance").getValue().toString();
                        TagsObject obj = new TagsObject(tagName, gender, mAgeMin, mAgeMax, mDistance);
                        myTagsList.add(obj);
                    }
                    if (getContext() != null) {
                        adapter = new TagsManagerAdapter(getContext(), myTags);
                    }
                    recyclerView.setAdapter(adapter);
                } else {
                    myTags.add("Add tags in options first!");
                    if (getContext() != null) {
                        adapter = new TagsManagerAdapter(getContext(), myTags);
                    }
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getUsersFromDb() {
        first = new ArrayList<>();
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference newUserDb = FirebaseDatabase.getInstance().getReference().child("Users");
        String newCurrentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Single<List<cards>> sinlgeObs1 = Single.create(emitter -> {
            // register onChange callback to database
            // callback will be called, when a value is available
            // the Single will stay open, until emitter#onSuccess is called with a collected list.
            newUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("maingetTag", "datasnapshot " + dataSnapshot);
                    if (dataSnapshot.child(currentUID).child("sex").exists()) {
                        Log.d("maingetTag", "myInfo.put sex ");
                        myInfo.put("sex", dataSnapshot.child(currentUID).child("sex").getValue().toString());
                    }
                    if (dataSnapshot.child(currentUID).child("dateOfBirth").exists()) {
                        int myAge = new StringDateToAge().stringDateToAge(dataSnapshot.child(currentUID).child("dateOfBirth").getValue().toString());
                        Log.d("maingetTag", "myInfo.put myage ");
                        myInfo.put("age", String.valueOf(myAge));
                    }
                    if (dataSnapshot.child(currentUID).child("connections").child("yes").exists()) {
                        for (DataSnapshot ds : dataSnapshot.child(currentUID).child("connections").child("yes").getChildren()) {
                            Log.d("rxJavaEmitter", "myUsers: ");
                            if (!dataSnapshot.child(currentUID).child("connections").child("matches").hasChild(ds.getKey()) && !dataSnapshot.child(ds.getKey()).child("connections").child("nope").hasChild(currentUID)) {
                                Log.d("rxJava", "onDataChangeFirst: " + ds.getKey() + "  myID " + currentUID);
                                Log.d("MaindataSnapshot", "dataSnapshot: " + dataSnapshot.child(ds.getKey()).child("connections").child("nope") + "  myID " + currentUID);
                                first.add(ds.getKey());
                                getTagsPreferencesUsers(dataSnapshot.child(ds.getKey()), true);
                            }
                        }
                    }
                    Log.d("rxMergeJavaSingle", "sinlgeObs1: " + rowItemsRxJava.toString());
                    List<cards> likedMeList = rowItemsRxJava;
                    emitter.onSuccess(likedMeList); //return collected data from database here...
                    rowItemsRxJava.clear();
                    Log.d("rowItemsRxJava", "rowItemsRxJava DELETE OBS1: " + rowItemsRxJava.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            // do some stuff
            emitter.setCancellable(new Cancellable() {
                @Override
                public void cancel() throws Exception {
                    //clean memory
                }
            });
        });
        Single<List<cards>> sinlgeObs2 = Single.create(emitter -> {
            // register onChange callback to database
            // callback will be called, when a value is available
            // the Single will stay open, until emitter#onSuccess is called with a collected list.
            newUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.d("rxJavaEmitter", "allUsers: ");
                        if (ds.child("sex").getValue() != null) {
                            if (ds.exists() && !first.contains(ds.getKey()) && !ds.child("connections").child("nope").hasChild(currentUID) && !ds.child("connections").child("yes").hasChild(currentUID) && !ds.getKey().equals(newCurrentUID)) {
                                ds.getKey().equals(currentUID);
                                Log.d("first", "OnChillAdded: " + ds.getKey());
                                Log.d("rxJava", "onDataChangeSecound2222: " + ds.getKey());
                                getTagsPreferencesUsers(ds, false);
                            }
                        }
                    }
                    Log.d("rxMergeJavaSingle", "sinlgeObs2: " + rowItemsRxJava.toString());
                    noMoreEditText.setText("There is no more users");
                    List<cards> notLikedMeList = rowItemsRxJava;
                    emitter.onSuccess(notLikedMeList);
                    rowItemsRxJava.clear();
                    Log.d("rowItemsRxJava", "rowItemsRxJava DELETE OBS2: " + rowItemsRxJava.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            // do some stuff
            emitter.setCancellable(new Cancellable() {
                @Override
                public void cancel() throws Exception {
                    //clean memory
                }
            });
        });
        Observable.merge(sinlgeObs1.toObservable(), sinlgeObs2.toObservable())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<List<cards>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d("rxMergeJava", "onSubscribe: ");
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<cards> o) {
                        if (sortByDistance.equals("true")) {
                            o = sortCollection(o);
                            rowItems.addAll(o);
                        } else {
                            rowItems.addAll(o);
                        }
                        Log.d("rxMergeJava", "onNext: " + o.toString());
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d("rxMergeJava", "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d("rxMergeJava", "onComplete: ");
                        for (cards cs : rowItems) {
                            Log.d("rxMergeJava: ", cs.getName() + "   dist: " + cs.getDistance());
                        }
                        if (getActivity() != null) {
                            arrayAdapter = new arrayAdapter(getActivity(), R.layout.item, rowItems);
                            flingContainer.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void getTagsPreferencesUsers(DataSnapshot ds, Boolean likesMe) {
        ArrayList<String> mutalTagsList = new ArrayList<>();
        StringBuilder mutalTagsSB = new StringBuilder();
        Map<Object, Object> tagsMap = new HashMap<>();
        try {
            Log.d("maingetTag", "User Name " + ds.child("name").getValue().toString());
            int age = new StringDateToAge().stringDateToAge(ds.child("dateOfBirth").getValue().toString());
            Log.d("maingetTag", "try 2");
            int myAge = Integer.parseInt(myInfo.get("age"));
            double latitude = Double.parseDouble(ds.child("location").child("latitude").getValue().toString());
            double longitude = Double.parseDouble(ds.child("location").child("longitude").getValue().toString());
            double distanceDouble = new CalculateDistance().distance(myLatitude, myLongitude, latitude, longitude);
            for (DataSnapshot dataTag : ds.child("tags").getChildren()) {
                Log.d("maingetTag", "forFirst ");
                for (TagsObject tag : myTagsList) {
                    ///VALIDATING MY PREFERENCES
                    //comparing tags
                    Log.d("maingetTag", "1st if: " + dataTag.getKey() + " == " + tag.getTagName());
                    if (dataTag.getKey().equals(tag.getTagName())) {
                        //validating my gender preferences
                        Log.d("maingetTag", "2nd if: " + tag.getGender() + " == " + ds.child("sex").getValue().toString() + "  ||  " + tag.getGender() + " == Any");
                        if (tag.getGender().equals(ds.child("sex").getValue().toString()) || tag.getGender().equals("Any")) {
                            Log.d("maingetTag", "3rd if: " + dataTag.child("gender").getValue().toString() + " == " + myInfo.get("sex") + "  ||  " + dataTag.child("gender").getValue().toString() + " == Any");
                            //validating user gender preferences
                            if (dataTag.child("gender").getValue().toString().equals(myInfo.get("sex")) || dataTag.child("gender").getValue().toString().equals("Any")) {
                                Log.d("maingetTag", "4th if: " + tag.getmAgeMin() + " <= " + age + "  &&  " + tag.getmAgeMax() + " >= " + age);
                                //validating myTag age preferences with minAge and maxAge
                                if (Integer.parseInt(tag.getmAgeMin()) <= age && Integer.parseInt(tag.getmAgeMax()) >= age) {
                                    Log.d("maingetTag", "5th if: " + dataTag.child("minAge").getValue().toString() + " <= " + myAge + "  &&  " + dataTag.child("maxAge").getValue().toString() + " >= " + myAge);
                                    //validating userTag age preferences with minAge and maxAge
                                    if (Integer.parseInt(dataTag.child("minAge").getValue().toString()) <= myAge && Integer.parseInt(dataTag.child("maxAge").getValue().toString()) >= myAge) {
                                        Log.d("maingetTag", "6th if: " + tag.getmDistance() + " >= " + distanceDouble);
                                        //validating myTag distance preference
                                        if (Double.parseDouble(tag.getmDistance()) >= distanceDouble) {
                                            //validate userTag distance preference
                                            Log.d("maingetTag", "7th if: " + dataTag.child("maxDistance").getValue().toString() + " >= " + distanceDouble);
                                            if (Double.parseDouble(dataTag.child("maxDistance").getValue().toString()) >= distanceDouble) {
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
                cards item = new cards(ds.getKey(), ds.child("name").getValue().toString(), profileImageUrl, mutalTagsSB.toString(), tagsMap, distanceDouble, likesMe);
                rowItemsRxJava.add(item);
                Log.d("rowItemsRxJava", "rowItemsRxJava: " + rowItemsRxJava.toString());
                Log.d("rxMergeJavaLoop: ", item.getName().toString() + " likesMe " + likesMe);
            } else {
            }
        } catch (Exception e) {
            Log.d("maingetTag", "tryError " + e.toString());
        }
    }

    private List<cards> sortCollection(List<cards> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(cards::getDistance));
        } else {
            Collections.sort(list, new Comparator<cards>() {
                public int compare(cards o1, cards o2) {
                    if (o1.getDistance() == o2.getDistance())
                        return 0;
                    return o1.getDistance() < o2.getDistance() ? -1 : 1;
                }
            });
        }
        return list;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMyInfo();
        fillTagsAdapter();
        Log.d("mainActivity", "rowItems on Start: " + rowItems.size());
        Log.d("mainfragmentfunctions", "onStart: " + rowItems.size());
        if (rowItems.size() == 0) {
            getUsersFromDb();
        }
    }

    private void updateMyInfo() {
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myLatitude = Double.parseDouble(dataSnapshot.child("location").child("latitude").getValue().toString());
                    myLongitude = Double.parseDouble(dataSnapshot.child("location").child("longitude").getValue().toString());
                    if (dataSnapshot.child("sortByDistance").exists()) {
                        sortByDistance = dataSnapshot.child("sortByDistance").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    @Override
    public void onResume() {
        Log.d("mainfragmentfunctions", "onResume: " + rowItems.size());
        checkUserStatus();
        super.onResume();
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mUID = user.getUid();
            //save UID of current signin user in shared preferences
            SharedPreferences sp = getActivity().getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();
        }
    }

    private void sendNotification(String matchId, String myName, String sendMessageText) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(matchId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(currentUID, R.drawable.login_photo, "Check out now!", "New match!", matchId);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
