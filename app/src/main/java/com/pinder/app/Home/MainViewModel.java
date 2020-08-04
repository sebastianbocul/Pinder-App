package com.pinder.app.Home;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pinder.app.Notifications.Token;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MainViewModel extends AndroidViewModel {
    MainRepository mainRepository;
    MutableLiveData<ArrayList<cards>> rowItemsLD = new MutableLiveData<>();
    MutableLiveData<ArrayList<String>> myTagsAdapterLD = new MutableLiveData<>();
    public MainViewModel(@NonNull Application application) {
        super(application);
        mainRepository = MainRepository.getInstance(application);
    }
//    public MainViewModel() {
//        mainRepository = MainRepository.getInstance(context);
//    }

    public MutableLiveData<Double> getMyLatitude() {
        return mainRepository.getMyLatitude();
    }

    public MutableLiveData<Double> getMyLongitude() {
        return mainRepository.getMyLongitude();
    }

    public MutableLiveData<ArrayList<String>> getMyTagsAdapterLD() {
        myTagsAdapterLD = mainRepository.getMyTagsAdapterLD();
        return myTagsAdapterLD;
    }


    public MutableLiveData<ArrayList<cards>> getRowItemsLD() {
        rowItemsLD = mainRepository.getRowItemsLD();
        return rowItemsLD;
    }

    public void getUsersFromDb() {
        mainRepository.getUsersFromDb();
    }

    public void isConnectionMatch(cards obj, Context con) {
        mainRepository.isConnectionMatch(obj, con);
    }

    void removeFirstObjectInAdapter() {
        ArrayList<cards> rowItems = rowItemsLD.getValue();
        if (rowItems.size() != 0) {
            rowItems.remove(0);
            rowItemsLD.postValue(rowItems);
        }
    }

    protected void updateToken(){
        mainRepository.updateToken();
    }

    protected void checkUserStatus(Context context){
        mainRepository.checkUserStatus(context);
    }

    public void onLeftCardExit(cards dataObject){
        mainRepository.onLeftCardExit(dataObject);
    }
}
