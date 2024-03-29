package com.pinder.app.persistance;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pinder.app.MainPopUpActivity;
import com.pinder.app.R;
import com.pinder.app.models.Card;
import com.pinder.app.models.TagsObject;
import com.pinder.app.notifications.Token;
import com.pinder.app.ui.dialogs.SharedPreferencesHelper;
import com.pinder.app.util.Constants;
import com.pinder.app.util.MultiTaskHandler;
import com.pinder.app.util.Resource;
import com.pinder.app.util.SendFirebaseNotification;
import com.pinder.app.util.StringDateToAge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import static com.pinder.app.util.SortingFunctions.sortTagsCollectionByDistance;
import static com.pinder.app.util.ValidateUserByPreferences.validateUserByPreferences;

public class MainFirebase {
    private static final String TAG = "MainFirebase";
    //counters- logs helpers
    int myCounterOnKeyExit = 0;
    int myCounterOnKeyEnter = 0;
    private final Context context;
    private ArrayList<Card> cardsArray = new ArrayList<Card>();
    private final Map<String, String> myInfo = new HashMap<>();
    private final ArrayList<TagsObject> myTagsListTemp = new ArrayList<>();
    private ArrayList<TagsObject> myTagsList = new ArrayList<>();
    private String sortByDistance = "false";
    //change later// temp solution
    private final MutableLiveData<Resource<ArrayList<Card>>> cardsArrayLD = new MutableLiveData<>();
    private LatLng myLoc;
    private String mUID;
    private final ArrayList<String> usersIdLikesMe = new ArrayList<>();

    public MainFirebase(Context context) {
        this.context = context;
    }

    public void fetchDataOrUpdateLocationAndFetchData() {
        if (myLoc == null) {
            updateLocation(context);
        } else {
            updateMyTagsAndPreferences();
        }
    }

    public void updateMyTagsAndPreferences() {
        ArrayList<String> myTagsAdapter = new ArrayList<>();
        Single<List<TagsObject>> updateTagsSingleObs = Single.create(emitter -> {
            String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference currentUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
            myTagsAdapter.clear();
            myTagsListTemp.clear();
            currentUserDatabaseReference.child("tags").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            myTagsAdapter.add("#" + ds.getKey());
                            String tagName = ds.getKey().toLowerCase();
                            String gender = ds.child("gender").getValue().toString();
                            String mAgeMax = ds.child("maxAge").getValue().toString();
                            String mAgeMin = ds.child("minAge").getValue().toString();
                            String mDistance = ds.child("maxDistance").getValue().toString();
                            TagsObject obj = new TagsObject(tagName, gender, mAgeMin, mAgeMax, mDistance);
                            myTagsListTemp.add(obj);
                        }
                        emitter.onSuccess(myTagsListTemp);
                    } else {
                        if (myTagsListTemp.size() == 0) {
                            myTagsAdapter.clear();
                            myTagsList.clear();
                            cardsArray.clear();
                            cardsArrayLD.postValue(Resource.emptydata(cardsArray));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            emitter.setCancellable(new Cancellable() {
                @Override
                public void cancel() throws Exception {
                    //clean memory
                }
            });
        });
        Single<String> updateSortByDistancePreferenceSingleObs = Single.create(emitter -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            sortByDistance = prefs.getString("sortByDistance", "notFound");
            Log.d(TAG, "no prefs found: " + sortByDistance);
            if (sortByDistance.equals("true") || sortByDistance.equals("false")) {
                Log.d(TAG, "sortByDistanceTemp: " + sortByDistance);
                emitter.onSuccess(sortByDistance);
            } else {
                String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
                ds.child("sortByDistance").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "no prefs found: downloading");
                        if (snapshot.exists()) sortByDistance = snapshot.getValue().toString();
                        emitter.onSuccess(sortByDistance);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            emitter.setCancellable(new Cancellable() {
                @Override
                public void cancel() throws Exception {
                    //clean memory
                }
            });
        });
        Observable.merge(updateTagsSingleObs.toObservable(), updateSortByDistancePreferenceSingleObs.toObservable())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Object o) {
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        boolean tagsNotChanged = Arrays.equals(myTagsList.toArray(), myTagsListTemp.toArray());
                        Log.d("RxOnComplete", "onComplete !sortByDistance.equals(sortByDistanceTemp) : " + !sortByDistance.equals(sortByDistance));
                        Log.d("RxOnComplete", "TagsNotChanged : " + tagsNotChanged);
                        if (!tagsNotChanged) {
                            Log.d("RxOnComplete", "onComplete if2: " + true);
                            myTagsList.clear();
                            myTagsList.addAll(myTagsListTemp);
                            myTagsListTemp.clear();
                            getUsersFromDb();
                        }
                    }
                });
    }

    protected void getUsersFromDb() {
        cardsArray.clear();
        cardsArrayLD.postValue(Resource.loading(cardsArray));
        Single<List<Card>> fetchUsersLikeMeSingleObs = createFetchUsersLikedMeObservable();
        Single<List<Card>> fetchCurrentUserInfoSingleObs = createUpdateMyInfoObservable();
        Single<List<Card>> fetchUsersInRangeSingleObs = createFetchInRangeObservable();
        Observable.concat(fetchCurrentUserInfoSingleObs.toObservable(), fetchUsersLikeMeSingleObs.toObservable(), fetchUsersInRangeSingleObs.toObservable())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Card>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Card> o) {
                        Log.d(TAG, "onNext: ");
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        cardsArrayLD.postValue(Resource.error(e.toString(), cardsArray));
                        Log.e(TAG, "onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                        Log.d(TAG, "Number of cards: " + cardsArray.size());
                        cardsArrayLD.postValue(Resource.success(cardsArray));
                    }
                });
    }

    private Single<List<Card>> createUpdateMyInfoObservable() {
        DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Single<List<Card>> fetchLikedMeUsersSingleObs = Single.create(emitter -> {
            usersDatabaseReference.child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange:  " + dataSnapshot.getKey() + " :" + dataSnapshot);
                    if (dataSnapshot.child("sex").exists()) {
                        myInfo.put("sex", dataSnapshot.child("sex").getValue().toString());
                    }
                    if (dataSnapshot.child("dateOfBirth").exists()) {
                        int myAge = new StringDateToAge().stringDateToAge(dataSnapshot.child("dateOfBirth").getValue().toString());
                        myInfo.put("age", String.valueOf(myAge));
                    }
                    Log.d(TAG, "UPPPDATE MY INFO: ");
                    emitter.onSuccess(cardsArray);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    emitter.onSuccess(cardsArray);
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
        return fetchLikedMeUsersSingleObs;
    }

    private Single<List<Card>> createFetchUsersLikedMeObservable() {
        DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Single<List<Card>> fetchUsersLikedMeSingleObs = Single.create(emitter -> {
            usersDatabaseReference.child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int nrOfChildren = (int) dataSnapshot.child("connections").child("yes").getChildrenCount();
                    Log.d(TAG, "nr of children " + nrOfChildren);
                    final MultiTaskHandler multiTaskHandler = new MultiTaskHandler(nrOfChildren) {
                        @Override
                        protected void onAllTasksCompleted() {
                            Log.d(TAG, "onAllTasksCompleted: LIKED ME");
                            //put the code that runs when all the tasks are complete here
                            emitter.onSuccess(cardsArray);
                        }
                    };
                    if (dataSnapshot.child("connections").child("yes").exists()) {
                        for (DataSnapshot ds : dataSnapshot.child("connections").child("yes").getChildren()) {
                            if (!dataSnapshot.child("connections").child("matches").hasChild(ds.getKey())) {
                                usersDatabaseReference.child(ds.getKey()).child("connections").child("nope").child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getValue() == null) {
                                            usersDatabaseReference.child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    usersIdLikesMe.add(snapshot.getKey());
                                                    Card card = validateUserByPreferences(snapshot, true, myLoc, myTagsList, myInfo);
                                                    Log.d(TAG, "from users liked me: before validation " + snapshot.child("name").getValue() + "  card: " + card);
                                                    if (card != null) {
                                                        Log.d(TAG, "from users liked me:  after validation" + snapshot.child("name").getValue());
                                                        cardsArray.add(card);
                                                    }
                                                    multiTaskHandler.taskComplete();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                        } else {
                                            multiTaskHandler.taskComplete();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            } else {
                                multiTaskHandler.taskComplete();
                            }
                        }
                    } else {
                        emitter.onSuccess(cardsArray);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    emitter.onSuccess(cardsArray);
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
        return fetchUsersLikedMeSingleObs;
    }

    private Single<List<Card>> createFetchInRangeObservable() {
        DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Single<List<Card>> fetchUsersInRangeSingleObs = Single.create(emitter -> {
            Log.d(TAG, "getUsersFromDb: latidute:" + myLoc.latitude + " longitude: " + myLoc.longitude);
            myTagsList = (ArrayList<TagsObject>) sortTagsCollectionByDistance(myTagsList);
            double maxSearchDistance = Double.parseDouble(myTagsList.get(myTagsList.size() - 1).getmDistance());
            Log.d(TAG, "getUsersFromDb: " + maxSearchDistance);
            DatabaseReference geoFireDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Geofire");
            geoFireDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("usersInGeoFire", "usersIngeofire: " + snapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            GeoFire geoFire = new GeoFire(geoFireDatabaseReference);
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(myLoc.latitude, myLoc.longitude), maxSearchDistance);
            ArrayList<String> usersIdGeoFire = new ArrayList<>();
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    myCounterOnKeyEnter++;
                    usersIdGeoFire.add(key);
                }

                @Override
                public void onKeyExited(String key) {
                    myCounterOnKeyExit++;
                    Log.d(TAG, "onKeyExited: " + key);
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                    Log.d(TAG, "onKeyMoved: " + key);
                }

                @Override
                public void onGeoQueryReady() {
                    Log.d(TAG, "onGeoQueryReady: ");
                    final MultiTaskHandler multiTaskHandler = new MultiTaskHandler(usersIdGeoFire.size()) {
                        @Override
                        protected void onAllTasksCompleted() {
                            //put the code that runs when all the tasks are complete here
                            Log.d(TAG, "onAllTasksCompleted: GEOQUERY ");
                            emitter.onSuccess(cardsArray);
                        }
                    };
//                    Log.d(TAG, "Number of users in geofire: " + usersIdGeoFire.size());
//                    Log.d("usersInGeoFire", "geofire: usersInArray: " + usersIdGeoFire.size());
//                    Log.d("usersInGeoFire", "geofire: onKeyEnter: " + myCounterOnKeyEnter);
//                    Log.d("usersInGeoFire", "geofire: onKeyExit: " + myCounterOnKeyExit);
                    for (String userId : usersIdGeoFire) {
                        usersDatabaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot ds) {
//                                Log.d("fetchWithGeofire", "onDataChange: " + ds);
                                if (ds.child("sex").getValue() != null) {
                                    if (ds.exists() && !usersIdLikesMe.contains(ds.getKey()) && !ds.child("connections").child("nope").hasChild(currentUID) && !ds.child("connections").child("yes").hasChild(currentUID) && !ds.getKey().equals(currentUID)) {
//                                        Log.d(TAG, "from geofire: " + ds.child("name").getValue());
                                        Card card = validateUserByPreferences(ds, false, myLoc, myTagsList, myInfo);
//                                        Card card=null;
                                        if (card != null) {
                                            cardsArray.add(card);
                                        }
                                    }
                                }
                                multiTaskHandler.taskComplete();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                multiTaskHandler.taskComplete();
                            }
                        });
                    }
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                    emitter.onError(new Throwable("onGeoQueryError"));
                    Log.d(TAG, "onGeoQueryError: ");
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
        return fetchUsersInRangeSingleObs;
    }

    public void isConnectionMatch(Card obj, Context con) {
        String userId = obj.getUserId();
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersDatabaseReference.child(userId).child("connections").child("yes").child(currentUID).setValue(true);
        DatabaseReference currentUserDatabaseReference = usersDatabaseReference.child(currentUID).child("connections").child("yes").child(userId);
        currentUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(con, "New connection!", Toast.LENGTH_LONG).show();
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    usersDatabaseReference.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUID).child("ChatId").setValue(key);
                    usersDatabaseReference.child(currentUID).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                    usersDatabaseReference.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUID).child("mutualTags").updateChildren(obj.getMutualTagsMap());
                    usersDatabaseReference.child(currentUID).child("connections").child("matches").child(dataSnapshot.getKey()).child("mutualTags").updateChildren(obj.getMutualTagsMap());
                    String matchId = dataSnapshot.getKey();
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String myName = " ";
                            if (dataSnapshot.child("name").exists()) {
                                myName = dataSnapshot.child("name").getValue().toString();
                            }
                            String myProfileImageUrl = "default";
                            if (dataSnapshot.child("profileImageUrl").exists()) {
                                myProfileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                            }
                            SendFirebaseNotification.sendNotification(matchId, currentUID, myProfileImageUrl, myName, context.getString(R.string.notification_body_match));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    //popactivity when matched
                    Intent i = new Intent(con, MainPopUpActivity.class);
                    i.putExtra("matchUser", obj);
                    con.startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void updateToken() {
        Log.d("FIREBASEMESSAGE", "updateToken: ");
        String token = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        Log.d("FIREBASEMESSAGE", "token: " + token);
        Log.d("FIREBASEMESSAGE", "mUID: " + mUID);
        ref.child(mUID).setValue(mToken);
    }

    public void checkUserStatus(Context context) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mUID = user.getUid();
            //save UID of current signin user in shared preferences
            SharedPreferencesHelper.setCurrentUserID(context, mUID);
        }
    }

    public void onLeftCardExit(Card dataObject) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUID = mAuth.getCurrentUser().getUid();
        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        String userId = dataObject.getUserId();
        usersDb.child(userId).child("connections").child("nope").child(currentUID).setValue(true);
    }

    public void updateLocation(Context context) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference usersDb;
        String currentUID = mAuth.getCurrentUser().getUid();
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference myRef = usersDb.child(currentUID).child("location");
        //geofire
        DatabaseReference geofire = FirebaseDatabase.getInstance().getReference().child("Geofire");
        DatabaseReference geofireUser = geofire.child(currentUID);
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.requestLocationPermission);
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                Log.d("updateLocation", "location: " + location);
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        myLoc = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        myRef.child("longitude").setValue(addresses.get(0).getLongitude());
                        myRef.child("latitude").setValue(addresses.get(0).getLatitude());
                        Log.d("updateLocation", "myLatitude updateLocation: " + myLoc.latitude);
                        Log.d("updateLocation", "myLongitude updateLocation: " + myLoc.longitude);
                        GeoFire geoFire = new GeoFire(geofire);
                        geoFire.setLocation(geofireUser.getKey(), new GeoLocation(myLoc.latitude, myLoc.longitude), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                            }
                        });
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
                        updateMyTagsAndPreferences();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    geofireUser.child("l").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Log.d("updateLocation", "onDataChange: " + snapshot);
                                Log.d("updateLocation", "onDataChange: " + snapshot.getKey());
                                double lon = (Double.parseDouble(snapshot.child("0").getValue().toString()));
                                double lat = (Double.parseDouble(snapshot.child("1").getValue().toString()));
                                myLoc = new LatLng(lat, lon);
                                updateMyTagsAndPreferences();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
    }
    public String getSortByDistance(){
        return sortByDistance;
    }
    public LiveData<Resource<ArrayList<Card>>> getCardsArrayLD() {
        return cardsArrayLD;
    }
}
