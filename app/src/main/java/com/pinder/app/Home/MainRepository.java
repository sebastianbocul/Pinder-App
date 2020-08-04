package com.pinder.app.Home;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class MainRepository {
    public static MainRepository instance = null;
    public static MainFirebase mainFirebase;

    public static MainRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MainRepository();
            mainFirebase = MainFirebase.getInstance(context);
        }
        return instance;
    }

    public MutableLiveData<Double> getMyLatitude() {
        return mainFirebase.getMyLatitude();
    }

    public MutableLiveData<Double> getMyLongitude() {
        return mainFirebase.getMyLongitude();
    }

    public MutableLiveData<ArrayList<String>> getMyTagsAdapterLD() {
        return mainFirebase.getMyTagsAdapterLD();
    }

    public MutableLiveData<ArrayList<cards>> getRowItemsLD() {
        return mainFirebase.getRowItemsLD();
    }

    public void getUsersFromDb() {
        mainFirebase.updateMyTagsAndSortBydDist();
    }

    public void isConnectionMatch(cards obj, Context con) {
        mainFirebase.isConnectionMatch(obj, con);
    }

    protected void updateToken() {
        mainFirebase.updateToken();
    }

    protected void checkUserStatus(Context context) {
        mainFirebase.checkUserStatus(context);
    }

    public void onLeftCardExit(cards dataObject) {
        mainFirebase.onLeftCardExit(dataObject);
    }
}
