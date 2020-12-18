package com.pinder.app.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.pinder.app.models.Card;
import com.pinder.app.persistance.MainFirebase;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public class MainRepository {
    public MainFirebase mainFirebase;

    public MainRepository(MainFirebase mainFirebase) {
        this.mainFirebase = mainFirebase;
    }

    public MutableLiveData<Resource<ArrayList<Card>>> getCardsArrayLD() {
        return mainFirebase.getCardsArrayLD();
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
}
