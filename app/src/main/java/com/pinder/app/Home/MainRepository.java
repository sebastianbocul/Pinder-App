package com.pinder.app.Home;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class MainRepository {
    public static MainRepository instance = null;
    public static MainFirebase mainFirebase;

    public static MainRepository getInstance(Context context){
        if(instance==null){
            instance= new MainRepository();
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

    public MutableLiveData<ArrayList<cards>> getRowItemsLD() {
        return mainFirebase.getRowItemsLD();
    }

    public void getUsersFromDb(){
        mainFirebase.updateMyTagsAndSortBydDist();
    }

}
