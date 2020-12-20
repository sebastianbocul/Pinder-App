package com.pinder.app.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.pinder.app.cache.MainCache;
import com.pinder.app.models.Card;
import com.pinder.app.persistance.MainFirebase;
import com.pinder.app.util.bound_resources.ConstantNetworkBoundResource;
import com.pinder.app.util.Resource;
import com.pinder.app.util.bound_resources.MainNetworkBoundResource;

import java.util.ArrayList;

public class MainRepository {
    public MainFirebase mainFirebase;
    public MainCache mainCache;
    private static final String TAG = "MainRepository";
    public MainRepository(MainFirebase mainFirebase, MainCache mainCache) {
        this.mainFirebase = mainFirebase;
        this.mainCache = mainCache;
    }

    public LiveData<Resource<ArrayList<Card>>> getCardsArrayLD() {
        return new MainNetworkBoundResource<ArrayList<Card>, ArrayList<Card>>() {
            @Override
            protected void saveFirebaseResult(@NonNull ArrayList<Card> item) {
                Log.d(TAG, "saveFirebaseResult: ");
                mainCache.saveCards(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable ArrayList<Card> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ArrayList<Card>> loadFromDb() {
                Log.d(TAG, "loadFromDb: ");
                return mainCache.getCards();
            }

            @NonNull
            @Override
            protected LiveData<Resource<ArrayList<Card>>> createFirebaseCall() {
                Log.d(TAG, "createFirebaseCall: ");
                return mainFirebase.getCardsArrayLD();
            }
        }.getAsLiveData();
    }

    public void fetchDataOrUpdateLocationAndFetchData() {
        mainFirebase.fetchDataOrUpdateLocationAndFetchData();
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

    public Boolean getSortByDistance() {
        return mainCache.getSortByDistance();
    }
    public String getSortByDistanceString(){
        return mainFirebase.getSortByDistance();
    }
}
