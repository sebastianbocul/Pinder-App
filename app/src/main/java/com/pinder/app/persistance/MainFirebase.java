package com.pinder.app.persistance;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pinder.app.MainPopUpActivity;
import com.pinder.app.R;
import com.pinder.app.models.Card;
import com.pinder.app.models.TagsObject;
import com.pinder.app.notifications.APIService;
import com.pinder.app.notifications.Client;
import com.pinder.app.notifications.Data;
import com.pinder.app.notifications.Sender;
import com.pinder.app.notifications.Token;
import com.pinder.app.util.CalculateDistance;
import com.pinder.app.util.MultiTaskHandler;
import com.pinder.app.util.Resource;
import com.pinder.app.util.StringDateToAge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

public class MainFirebase {
    private static final String TAG = "MainFirebase";
    public static MainFirebase instance = null;
    Context context;
    //    MutableLiveData<Double> myLongitude = new MutableLiveData<>();
//    MutableLiveData<Double> myLatitude = new MutableLiveData<>();
    ArrayList<Card> rowItems = new ArrayList<Card>();
    ArrayList<Card> rowItemsRxJava = new ArrayList<Card>();
    private Map<String, String> myInfo = new HashMap<>();
    ArrayList<TagsObject> myTagsListTemp = new ArrayList<>();
    ArrayList<TagsObject> myTagsList = new ArrayList<>();
    private String sortByDistance = "false";
    String sortByDistanceTemp = "false";
    //change later// temp solution
    MutableLiveData<Resource<ArrayList<Card>>> rowItemsLD = new MutableLiveData<>();
    public LatLng loc;

    public static MainFirebase getInstance(Application context2) {
        if (instance == null) {
            instance = new MainFirebase();
            instance.context = context2;
        }
        return instance;
    }

    MutableLiveData<ArrayList<String>> myTagsAdapterLD = new MutableLiveData<>();

    public void fetchDataOrUpdateLocationAndFetchData() {
        if (loc == null) {
            updateLocation(context);
        } else {
            updateMyTagsAndPreferences();
        }
    }

    public void updateMyTagsAndPreferences() {
        ArrayList<String> myTagsAdapter = new ArrayList<>();
        Log.d("MainFragment", "mainFirebase tags: " + myTagsAdapterLD.getValue());
        Single<List<TagsObject>> sinlgeObs1 = Single.create(emitter -> {
            String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
            myTagsAdapter.clear();
            myTagsListTemp.clear();
            ds.child("tags").addListenerForSingleValueEvent(new ValueEventListener() {
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
                            myTagsAdapterLD.postValue(myTagsAdapter);
                            myTagsList.clear();
                            rowItems.clear();
                            rowItemsLD.postValue(Resource.emptydata(rowItems));
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
        Single<String> sinlgeObs2 = Single.create(emitter -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            sortByDistanceTemp = prefs.getString("sortByDistance", "notFound");
            Log.d(TAG, "no prefs found: " + sortByDistanceTemp);
            if (sortByDistanceTemp.equals("true") || sortByDistanceTemp.equals("false")) {
                Log.d(TAG, "sortByDistanceTemp: " + sortByDistanceTemp.toString());
                emitter.onSuccess(sortByDistanceTemp);
            } else {
                String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
                ds.child("sortByDistance").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "no prefs found: downloading");
                        if (snapshot.exists()) sortByDistanceTemp = snapshot.getValue().toString();
                        emitter.onSuccess(sortByDistanceTemp);
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
        Observable.merge(sinlgeObs1.toObservable(), sinlgeObs2.toObservable())
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
                        boolean retval2 = Arrays.equals(myTagsList.toArray(), myTagsListTemp.toArray());
                        if (!retval2) {
                            Log.d("RxOnComplete", "onComplete: if1 " + retval2);
                            myTagsAdapterLD.postValue(myTagsAdapter);
                        }
                        Log.d("RxOnComplete", "onComplete !sortByDistance.equals(sortByDistanceTemp) : " + !sortByDistance.equals(sortByDistanceTemp));
                        Log.d("RxOnComplete", "onComplete retval2: " + retval2);
                        if (!sortByDistance.equals(sortByDistanceTemp) || !retval2) {
                            Log.d("RxOnComplete", "onComplete if2: " + true);
                            sortByDistance = sortByDistanceTemp;
                            myTagsList.clear();
                            for (TagsObject myTag : myTagsListTemp) {
                                myTagsList.add(myTag);
                            }
                            myTagsListTemp.clear();
                            getUsersFromDb();
                        }
                    }
                });
    }

    int myCounter = 0;
    protected void getUsersFromDb() {
        rowItems.clear();
        rowItemsLD.postValue(Resource.loading(rowItems));
        rowItemsRxJava.clear();
        ArrayList<String> first = new ArrayList<>();
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference newUserDb = FirebaseDatabase.getInstance().getReference().child("Users");
        //   String newCurrentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Single<List<Card>> fetchLikedMeUsersObservable = Single.create(emitter -> {
            //updates my sex and age, then takes users that liked me
            newUserDb.child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    int nrOfChildren = (int) dataSnapshot.child("connections").child("yes").getChildrenCount();
                    final MultiTaskHandler multiTaskHandler = new MultiTaskHandler(nrOfChildren) {
                        @Override
                        protected void onAllTasksCompleted() {
                            //put the code that runs when all the tasks are complete here
                            List<Card> notLikedMeList = rowItemsRxJava;
                            emitter.onSuccess(notLikedMeList);
                        }
                    };
                    Log.d(TAG, "getUsersFromDb: " + myCounter);
                    if (dataSnapshot.child("connections").child("yes").exists()) {
                        for (DataSnapshot ds : dataSnapshot.child("connections").child("yes").getChildren()) {
                            if (!dataSnapshot.child("connections").child("matches").hasChild(ds.getKey())) {
                                newUserDb.child(ds.getKey()).child("connections").child("nope").child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getValue() == null) {
                                            newUserDb.child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    first.add(snapshot.getKey());
                                                    getTagsPreferencesUsers(snapshot, true);
                                                    Log.d(TAG, "from users liked me: " + snapshot.child("name").getValue());
                                                    Log.d(TAG, "getUsersFromDb getUsers: " + myCounter);
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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    List<Card> notLikedMeList = rowItemsRxJava;
                    emitter.onSuccess(notLikedMeList);
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
        Single<List<Card>> fetchUsersInRangeObservable = Single.create(emitter -> {
            Log.d(TAG, "getUsersFromDb: latidute:" + loc.latitude + " longitude: " + loc.longitude);
            myTagsList = (ArrayList<TagsObject>) sortTagsCollectionByDistance(myTagsList);
            double maxDistance = Double.parseDouble(myTagsList.get(myTagsList.size() - 1).getmDistance());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Geofire");
            GeoFire geoFire = new GeoFire(ref);
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(loc.latitude, loc.longitude), maxDistance);
            ArrayList<String> usersIdGeoFire=new ArrayList<>();
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    Log.d(TAG, "onKeyEntered: " + key);
                    usersIdGeoFire.add(key);
                }

                @Override
                public void onKeyExited(String key) {
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
                            List<Card> notLikedMeList = rowItemsRxJava;
                            emitter.onSuccess(notLikedMeList);
                        }
                    };
                    Log.d(TAG, "Number of users in geofire: " + usersIdGeoFire.size());
                    for(String userId: usersIdGeoFire){
                        newUserDb.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot ds) {
                                Log.d("fetchWithGeofire", "onDataChange: " + ds);
                                if (ds.child("sex").getValue() != null) {
                                    if (ds.exists() && !first.contains(ds.getKey()) && !ds.child("connections").child("nope").hasChild(currentUID) && !ds.child("connections").child("yes").hasChild(currentUID) && !ds.getKey().equals(currentUID)) {
                                        ds.getKey().equals(currentUID);
                                        Log.d(TAG, "from geofire: " + ds.child("name").getValue());
                                        getTagsPreferencesUsers(ds, false);
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
                    List<Card> notLikedMeList = rowItemsRxJava;
                    emitter.onSuccess(notLikedMeList);
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
        Observable.merge(fetchLikedMeUsersObservable.toObservable(), fetchUsersInRangeObservable.toObservable())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Card>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Card> o) {
                        Log.d(TAG, "onNext: ");
//                        rowItems.addAll(o);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e(TAG, "onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                        rowItems.addAll(rowItemsRxJava);
                        if (sortByDistance.equals("true")) {
                            rowItems = sortCollectionByLikesMeThenDistance(rowItems);
                        } else {
                            rowItems = sortCollectionByLikesMe(rowItems);
                        }
                        for (Card c : rowItems) {
                            Log.d(TAG, "cards sorted: name: " + c.getName() + "  likesme : " + c.isLikesMe() + " dist: " + c.getDistance());
                        }
                        Log.d(TAG, "Number of cards: " + rowItems.size());
                        rowItemsLD.postValue(Resource.success(rowItems));
                    }
                });
    }

    private void getTagsPreferencesUsers(DataSnapshot ds, Boolean likesMe) {
        ArrayList<String> mutalTagsList = new ArrayList<>();
        StringBuilder mutalTagsSB = new StringBuilder();
        Map<Object, Object> tagsMap = new HashMap<>();
        try {
            Log.d("getTagsPreferencesUsers", "User Name " + ds.child("name").getValue().toString() + " uid: " + ds.getKey());
            int age = new StringDateToAge().stringDateToAge(ds.child("dateOfBirth").getValue().toString());
            Log.d("getTagsPreferencesUsers", "age: " + age);
            int myAge = Integer.parseInt(myInfo.get("age"));
            Log.d("getTagsPreferencesUsers", "myAge: " + myAge);
            double latitude = Double.parseDouble(ds.child("location").child("latitude").getValue().toString());
            Log.d("getTagsPreferencesUsers", "latitude: " + latitude);
            double longitude = Double.parseDouble(ds.child("location").child("longitude").getValue().toString());
            Log.d("getTagsPreferencesUsers", "longitude: " + longitude);
            double distanceDouble = CalculateDistance.distance(loc.latitude, loc.longitude, latitude, longitude);
            Log.d("getTagsPreferencesUsers", "distanceDouble" + distanceDouble);
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
                String gender = "";
                if (ds.child("sex").exists()) {
                    gender = ds.child("sex").getValue().toString();
                }
                String dateOfBirth = "";
                if (ds.child("dateOfBirth").exists()) {
                    dateOfBirth = ds.child("dateOfBirth").getValue().toString();
                }
                ArrayList images = new ArrayList();
                for (DataSnapshot dataSnapshot : ds.child("images").getChildren()) {
                    images.add(dataSnapshot.child("uri").getValue());
                }
                String location = "";
                if (ds.child("location").child("locality").exists()) {
                    location = ds.child("location").child("locality").getValue().toString();
                }
                String description = "";
                if (ds.child("description").exists()) {
                    description = ds.child("description").getValue().toString();
                }
                Card item = new Card(ds.getKey(), ds.child("name").getValue().toString(), profileImageUrl, images, gender, dateOfBirth, mutalTagsSB.toString(), tagsMap, distanceDouble, location, likesMe, description);
                rowItemsRxJava.add(item);
                Log.d("rowItemsRxJava", "rowItemsRxJava: " + rowItemsRxJava.toString());
                Log.d("rxMergeJavaLoop: ", item.getName().toString() + " likesMe " + likesMe);
            } else {
            }
        } catch (Exception e) {
            Log.d("maingetTag", "tryError " + e.toString());
        }
    }

    private List<Card> sortCollection(List<Card> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(Card::getDistance));
        } else {
            Collections.sort(list, new Comparator<Card>() {
                public int compare(Card o1, Card o2) {
                    if (o1.getDistance() == o2.getDistance())
                        return 0;
                    return o1.getDistance() < o2.getDistance() ? -1 : 1;
                }
            });
        }
        return list;
    }

    private ArrayList<Card> sortCollectionByLikesMe(ArrayList<Card> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(Card::isLikesMe).reversed());
        } else {
            Collections.sort(list, new Comparator<Card>() {
                public int compare(Card o1, Card o2) {
                    Boolean x1 = ((Card) o1).isLikesMe();
                    Boolean x2 = ((Card) o2).isLikesMe();
                    int sComp = x2.compareTo(x1);
                    return sComp;
                }
            });
        }
        return list;
    }

    private ArrayList<Card> sortCollectionByLikesMeThenDistance(ArrayList<Card> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(Card::isLikesMe).reversed().thenComparing(Card::getDistance));
        } else {
            Collections.sort(list, new Comparator<Card>() {
                public int compare(Card o1, Card o2) {
                    Boolean x1 = ((Card) o1).isLikesMe();
                    Boolean x2 = ((Card) o2).isLikesMe();
                    int sComp = x2.compareTo(x1);
                    if (sComp != 0) {
                        return sComp;
                    }
                    return o1.getDistance() < o2.getDistance() ? -1 : 1;
                }
            });
        }
        return list;
    }

    boolean notify = false;

    public void isConnectionMatch(Card obj, Context con) {
        //  this.context=context;
        String userId = obj.getUserId();
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        usersDb.child(userId).child("connections").child("yes").child(currentUID).setValue(true);
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUID).child("connections").child("yes").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(con, "New connection!", Toast.LENGTH_LONG).show();
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

    private void sendNotification(String matchId, String myName, String sendMessageText) {
        Client client = new Client();
        APIService apiService = client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(matchId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(currentUID, R.drawable.ic_logovector, "Check out now!", "New match!", matchId);
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

    private String mUID;

    public void updateToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    public void checkUserStatus(Context context) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mUID = user.getUid();
            //save UID of current signin user in shared preferences
            SharedPreferences sp = context.getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();
        }
    }

    public void onLeftCardExit(Card dataObject) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUID = mAuth.getCurrentUser().getUid();
        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        String userId = dataObject.getUserId();
        usersDb.child(userId).child("connections").child("nope").child(currentUID).setValue(true);
    }

    public MutableLiveData<Resource<ArrayList<Card>>> getRowItemsLD() {
        return rowItemsLD;
    }

    public MutableLiveData<ArrayList<String>> getMyTagsAdapterLD() {
        Log.d("MainFragment", "getTags firebase " + myTagsAdapterLD.getValue());
        return myTagsAdapterLD;
    }

    private List<TagsObject> sortTagsCollectionByDistance(List<TagsObject> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(TagsObject::getmDistance));
        } else {
            Collections.sort(list, new Comparator<TagsObject>() {
                public int compare(TagsObject o1, TagsObject o2) {
                    if (Double.parseDouble(o1.getmDistance()) == Double.parseDouble(o2.getmDistance()))
                        return 0;
                    return Double.parseDouble(o1.getmDistance()) < Double.parseDouble(o2.getmDistance()) ? -1 : 1;
                }
            });
        }
        return list;
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
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
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
                        loc = new LatLng((double) addresses.get(0).getLatitude(), (double) addresses.get(0).getLongitude());
                        myRef.child("longitude").setValue(addresses.get(0).getLongitude());
                        myRef.child("latitude").setValue(addresses.get(0).getLatitude());
                        Log.d("updateLocation", "myLatitude updateLocation: " + loc.latitude);
                        Log.d("updateLocation", "myLongitude updateLocation: " + loc.longitude);
                        GeoFire geoFire = new GeoFire(geofire);
                        geoFire.setLocation(geofireUser.getKey(), new GeoLocation(loc.latitude, loc.longitude), new GeoFire.CompletionListener() {
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
                                loc = new LatLng(lat, lon);
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
}
