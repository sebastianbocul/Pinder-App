package com.pinder.app.persistance;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.pinder.app.models.SettingInfoObject;
import com.pinder.app.util.Resource;

import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;

public class SettingsFirebase implements SettingsFirebaseDao {
    private static final String TAG = "SettingsFirebase";
    private SettingInfoObject bufferInfo = new SettingInfoObject();
    private MutableLiveData<String> date = new MutableLiveData<>();
    private MutableLiveData<Boolean> showMyLocation = new MutableLiveData<>();
    private MutableLiveData<Boolean> sortByDistance = new MutableLiveData<>();
    private MutableLiveData<Resource<Integer>> logoutLiveData = new MutableLiveData<>();
    public static SettingsFirebase instance = null;

    public static SettingsFirebase getInstance() {
        if (instance == null) {
            instance = new SettingsFirebase();
            instance.loadDataFromDb();
        }
        return instance;
    }

    private void loadDataFromDb() {
        Log.d(TAG, "loadDataFromDb:  ");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "mAuth: " + mAuth);
        Log.d(TAG, "mAuth.getCurrentUser(): " + mAuth.getCurrentUser());
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference myDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        myDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("dateOfBirth").exists()) {
                        String dateOfBirth = dataSnapshot.child("dateOfBirth").getValue().toString();
                        date.postValue(dateOfBirth);
                        bufferInfo.setDate(dateOfBirth);
                    }else {
                        date.postValue("01/01/2000");
                        bufferInfo.setDate("01/01/2000");
                        myDatabaseReference.child("dateOfBirth").setValue("01/01/2000");
                    }
                    if (dataSnapshot.child("showMyLocation").exists()) {
                        Boolean showMyLocationBool = (Boolean) dataSnapshot.child("showMyLocation").getValue();
                        showMyLocation.postValue(showMyLocationBool);
                        bufferInfo.setShowMyLocation(showMyLocationBool);
                    }else {
                        showMyLocation.postValue(false);
                        bufferInfo.setShowMyLocation(false);
                        myDatabaseReference.child("showMyLocation").setValue(false);
                    }
                    if (dataSnapshot.child("sortByDistance").exists()) {
                        Boolean sortByDistanceBool = (Boolean) dataSnapshot.child("sortByDistance").getValue();
                        sortByDistance.postValue(sortByDistanceBool);
                        bufferInfo.setSortByDistance(sortByDistanceBool);
                    }else {
                        sortByDistance.postValue(false);
                        bufferInfo.setSortByDistance(false);
                        myDatabaseReference.child("sortByDistance").setValue(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void updateMyDb(Boolean dateValid, int logoutFlag) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference myDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        if (showMyLocation.getValue() != null) {
            if (showMyLocation.getValue() != bufferInfo.getShowMyLocation()) {
                myDatabaseReference.child("showMyLocation").setValue(showMyLocation.getValue());
                bufferInfo.setShowMyLocation(showMyLocation.getValue());
            }
        }
        if (sortByDistance.getValue() != null) {
            if (sortByDistance.getValue() != bufferInfo.getSortByDistance()) {
                myDatabaseReference.child("sortByDistance").setValue(sortByDistance.getValue());
                bufferInfo.setSortByDistance(sortByDistance.getValue());
            }
        }
        if (date.getValue() == null) return;
        if (dateValid == false) {
            date.setValue(bufferInfo.getDate());
            return;
        }
        String dateOfBirth = date.getValue().toString();
        if (!dateOfBirth.equals(bufferInfo.getDate())) {
            myDatabaseReference.child("dateOfBirth").setValue(dateOfBirth);
            bufferInfo.setDate(dateOfBirth);
        }
        logoutLiveData.postValue(Resource.success(logoutFlag));
    }

    public void deleteWithRxJava(String userId) {
        logoutLiveData.postValue(Resource.loading(0));
        //delete Tags
        Observable<Object> deleteUserTagsObservable = Single.create(emitter -> {
            DatabaseReference usersTagReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("tags");
            DatabaseReference tagsReference = FirebaseDatabase.getInstance().getReference().child("Tags");
            usersTagReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.d(TAG,"delete: Tags/images/"+ds.getKey()+"/"+userId);
                        tagsReference.child(ds.getKey()).child(userId).removeValue();
                    }
                    emitter.onSuccess("finished");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }).toObservable();
        //delete Storage images
        Observable<Object> deleteStorageObservable = Single.create(emitter -> {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(userId);
            // Delete the userStorage
            storageRef.listAll()
                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            for (StorageReference item : listResult.getItems()) {
                                Log.d(TAG,"delete: Storage/images/"+userId+"/"+item.getName());

                                // All the items under listRef.
                                item.delete();
                            }
                            emitter.onSuccess("finished");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Uh-oh, an error occurred!
                        }
                    });
        }).toObservable();
        Observable<Object> deleteMatchesObservable = Single.create(emitter -> {
            DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users");
            users.child(userId).child("connections").child("matches").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        try {
                            Log.d(TAG,"delete: Users/" +ds.getKey()+"/connections/matches/"+userId);
                            Log.d(TAG,"delete: Users/" +ds.getKey()+"/connections/yes/"+userId);

                            users.child(ds.getKey()).child("connections").child("matches").child(userId).removeValue();
                            users.child(ds.getKey()).child("connections").child("yes").child(userId).removeValue();
                        } catch (Exception e) {
                         //   Toast.makeText(getContext(), "Oooops something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                    emitter.onSuccess("finished");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }).toObservable();
        //remove geofire
        Observable<Object> deleteUserGooFire = Single.create(emitter -> {
            DatabaseReference geoFireRef = FirebaseDatabase.getInstance().getReference().child("Geofire");
            geoFireRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(userId).exists()) {
                        Log.d(TAG,"delete: Geofire/" +userId);
                        geoFireRef.child(userId).removeValue();
                    }
                    emitter.onSuccess("finished");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }).toObservable();
        //remove token
        Observable<Object> deleteTokensObservable = Single.create(emitter -> {
            DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("Tokens");
            tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(userId).exists()) {
                        Log.d(TAG,"delete: Tokens/" +userId);
                        tokenRef.child(userId).removeValue();
                    }

                    emitter.onSuccess("finished");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }).toObservable();
        //delete chat mess
        Observable<Object> deleteChatObservable = Single.create(emitter -> {
            DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users");
            DatabaseReference chat = FirebaseDatabase.getInstance().getReference().child("Chat");
            users.child(userId).child("connections").child("matches").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        try {
                            Log.d(TAG,"delete: Chat/" +ds.child("ChatId").getValue().toString());
                            chat.child(ds.child("ChatId").getValue().toString()).removeValue();
                        } catch (Exception e) {
                            //   Toast.makeText(getContext(), "Oooops something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                    emitter.onSuccess("finished");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }).toObservable();

        Observable<Object> observable = Observable.merge(Arrays.asList(deleteUserTagsObservable,
                deleteMatchesObservable,
                deleteStorageObservable,
                deleteUserGooFire,
                deleteTokensObservable,
                deleteChatObservable));


        //remove user
        Observable<Object> deleteUserObservable = Single.create(emitter -> {
            DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
            mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG,"delete: User/"+userId);
                    if (dataSnapshot.exists()) {
                        mUserDatabase.removeValue();
                    }
                    emitter.onSuccess("finished");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }).toObservable();

        //remove data + user
        Observable.concat(observable, deleteUserObservable)
                .observeOn(AndroidSchedulers.mainThread())
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
                        logoutLiveData.postValue(Resource.success(1));
                    }
                });
    }

    public void restartMatches(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users");
        users.child(userId).child("connections").child("matches").removeValue();
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("connections").child("matches").child(userId).exists()) {
                        users.child(ds.getKey()).child("connections").child("matches").child(userId).removeValue();
                    }
                    if (ds.child("connections").child("yes").child(userId).exists()) {
                        users.child(ds.getKey()).child("connections").child("yes").child(userId).removeValue();
                    }
                    if (ds.child("connections").child("nope").child(userId).exists()) {
                        users.child(ds.getKey()).child("connections").child("nope").child(userId).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    public LiveData<String> getDate() {
        return date;
    }

    @Override
    public LiveData<Boolean> getSortByDistance() {
        return sortByDistance;
    }

    @Override
    public LiveData<Boolean> getShowMyLocation() {
        return showMyLocation;
    }

    @Override
    public void setDate(String date) {
        this.date.setValue(date);
    }

    @Override
    public void setSortByDistance(Boolean bool) {
        this.sortByDistance.postValue(bool);
    }

    @Override
    public void setShowMyLocation(Boolean bool) {
        this.showMyLocation.postValue(bool);
    }

    public MutableLiveData<Resource<Integer>> getLogoutLiveData() {
        return logoutLiveData;
    }
}
