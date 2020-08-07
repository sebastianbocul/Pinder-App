package com.pinder.app.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

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
import com.pinder.app.functions.CalculateDistance;
import com.pinder.app.functions.StringDateToAge;
import com.pinder.app.notifications.APIService;
import com.pinder.app.notifications.Client;
import com.pinder.app.notifications.Data;
import com.pinder.app.notifications.Sender;
import com.pinder.app.notifications.Token;
import com.pinder.app.PopActivity;
import com.pinder.app.R;
import com.pinder.app.tags.maintags.TagsObject;

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
    public static MainFirebase instance = null;
    Context context;
    MutableLiveData<Double> myLongitude = new MutableLiveData<>();
    MutableLiveData<Double> myLatitude = new MutableLiveData<>();
    ArrayList<cards> rowItems = new ArrayList<cards>();
    ArrayList<cards> rowItemsRxJava = new ArrayList<cards>();
    private Map<String, String> myInfo = new HashMap<>();
    ArrayList<TagsObject> myTagsListTemp = new ArrayList<>();
    ArrayList<TagsObject> myTagsList = new ArrayList<>();
    private String sortByDistance = "false";
    String sortByDistanceTemp = "false";
    //change later// temp solution
    MutableLiveData<ArrayList<cards>> rowItemsLD = new MutableLiveData<>();

    public static MainFirebase getInstance(Context context2) {
        if (instance == null) {
            instance = new MainFirebase();
            instance.context = context2;
            instance.updateLocation(context2);
        }
        return instance;
    }
    ArrayList<String> myTagsAdapter = new ArrayList<>();


    MutableLiveData<ArrayList<String>> myTagsAdapterLD = new MutableLiveData<>();
    public void updateMyTagsAndSortBydDist() {
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
                            rowItems.clear();
                            rowItemsLD.postValue(rowItems);
                            Toast.makeText(context, "Add tags first!", Toast.LENGTH_SHORT).show();
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
            String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
            ds.child("sortByDistance").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) sortByDistanceTemp = snapshot.getValue().toString();
                    emitter.onSuccess(sortByDistanceTemp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
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
                        if(!retval2){
                            myTagsAdapterLD.postValue(myTagsAdapter);
                        }
                        if (!sortByDistance.equals(sortByDistanceTemp) || !retval2) {

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

    public void updateLocation(Context context) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference usersDb;
        String currentUID = mAuth.getCurrentUser().getUid();
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        myLongitude.postValue(addresses.get(0).getLongitude());
                        myLatitude.postValue(addresses.get(0).getLatitude());
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

    protected void getUsersFromDb() {
        rowItems.clear();
        rowItemsLD.postValue(rowItems);
        rowItemsRxJava.clear();
        ArrayList<String> first = new ArrayList<>();
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference newUserDb = FirebaseDatabase.getInstance().getReference().child("Users");
        //   String newCurrentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Single<List<cards>> sinlgeObs1 = Single.create(emitter -> {
            // register onChange callback to database
            // callback will be called, when a value is available
            // the Single will stay open, until emitter#onSuccess is called with a collected list.
            newUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(currentUID).child("sex").exists()) {
                        myInfo.put("sex", dataSnapshot.child(currentUID).child("sex").getValue().toString());
                    }
                    if (dataSnapshot.child(currentUID).child("dateOfBirth").exists()) {
                        int myAge = new StringDateToAge().stringDateToAge(dataSnapshot.child(currentUID).child("dateOfBirth").getValue().toString());
                        myInfo.put("age", String.valueOf(myAge));
                    }
                    if (dataSnapshot.child(currentUID).child("connections").child("yes").exists()) {
                        for (DataSnapshot ds : dataSnapshot.child(currentUID).child("connections").child("yes").getChildren()) {
                            if (!dataSnapshot.child(currentUID).child("connections").child("matches").hasChild(ds.getKey()) && !dataSnapshot.child(ds.getKey()).child("connections").child("nope").hasChild(currentUID)) {
                                first.add(ds.getKey());
                                getTagsPreferencesUsers(dataSnapshot.child(ds.getKey()), true);
                            }
                        }
                    }
                    List<cards> likedMeList = rowItemsRxJava;
                    emitter.onSuccess(likedMeList); //return collected data from database here...
                    rowItemsRxJava.clear();
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
                        if (ds.child("sex").getValue() != null) {
                            if (ds.exists() && !first.contains(ds.getKey()) && !ds.child("connections").child("nope").hasChild(currentUID) && !ds.child("connections").child("yes").hasChild(currentUID) && !ds.getKey().equals(currentUID)) {
                                ds.getKey().equals(currentUID);
                                getTagsPreferencesUsers(ds, false);
                            }
                        }
                    }
                    List<cards> notLikedMeList = rowItemsRxJava;
                    emitter.onSuccess(notLikedMeList);
                    rowItemsRxJava.clear();
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
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<cards> o) {
                        if (sortByDistance.equals("true")) {
                            o = sortCollection(o);
                            rowItems.addAll(o);
                        } else {
                            rowItems.addAll(o);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        rowItemsLD.postValue(rowItems);
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
            double distanceDouble = new CalculateDistance().distance(myLatitude.getValue(), myLongitude.getValue(), latitude, longitude);
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

    boolean notify = false;

    public void isConnectionMatch(cards obj, Context con) {
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
                    Intent i = new Intent(con, PopActivity.class);
                    i.putExtra("matchId", matchId);
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
        ;
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

    protected void updateToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    protected void checkUserStatus(Context context) {
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

    public void onLeftCardExit(cards dataObject) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUID = mAuth.getCurrentUser().getUid();
        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        String userId = dataObject.getUserId();
        usersDb.child(userId).child("connections").child("nope").child(currentUID).setValue(true);
    }


    public MutableLiveData<Double> getMyLatitude() {
        return myLatitude;
    }

    public MutableLiveData<Double> getMyLongitude() {
        return myLongitude;
    }

    public MutableLiveData<ArrayList<cards>> getRowItemsLD() {
        return rowItemsLD;
    }

    public MutableLiveData<ArrayList<String>> getMyTagsAdapterLD() {
        return myTagsAdapterLD;
    }

}
