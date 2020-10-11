package com.pinder.app.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.pinder.app.models.Card;
import com.pinder.app.persistance.MainFirebase;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public class MainRepository {
    public static MainRepository instance = null;
    public static MainFirebase mainFirebase;

    public static MainRepository getInstance(Application context) {
        if (instance == null) {
            instance = new MainRepository();
            mainFirebase = MainFirebase.getInstance(context);
        }
        return instance;
    }

//    public MutableLiveData<Double> getMyLatitude() {
//        return mainFirebase.getMyLatitude();
//    }
//
//    public MutableLiveData<Double> getMyLongitude() {
//        return mainFirebase.getMyLongitude();
//    }

    public MutableLiveData<ArrayList<String>> getMyTagsAdapterLD() {
        return mainFirebase.getMyTagsAdapterLD();
    }

    public MutableLiveData<Resource<ArrayList<Card>>> getRowItemsLD() {
        return mainFirebase.getRowItemsLD();
    }

    public void updateMyTagsAndSortBydDist() {
        mainFirebase.updateMyTagsAndSortBydDist();
    }

    public void isConnectionMatch(Card obj, Context con) {
        mainFirebase.isConnectionMatch(obj, con);
    }

    public void updateToken() {
        mainFirebase.updateToken();
    }

    public void checkUserStatus(Context context) {
        mainFirebase.checkUserStatus(context);
    }

    public void onLeftCardExit(Card dataObject) {
        mainFirebase.onLeftCardExit(dataObject);
    }
}
