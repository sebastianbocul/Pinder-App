package com.pinder.app.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.pinder.app.models.Card;
import com.pinder.app.persistance.MainFirebase;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public class MainRepository {
    private static final String TAG = "MainRepository";
    public MainFirebase mainFirebase;

    public MainRepository(MainFirebase mainFirebase) {
        this.mainFirebase = mainFirebase;
    }

    public LiveData<Resource<ArrayList<Card>>> getCardsArrayLD() {
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

    public String getSortByDistanceString() {
        return mainFirebase.getSortByDistance();
    }

    public void removeCardFromArray(Card card) {
        Resource<ArrayList<Card>> cardsArray;
        cardsArray = mainFirebase.getCardsArrayLD().getValue();
        if (cardsArray != null && cardsArray.data != null) {
            cardsArray.data.remove(card);
        }
        if (cardsArray != null && cardsArray.data.size() == 0) {
            cardsArray = Resource.emptydata(cardsArray.data);
        }
        mainFirebase.getCardsArrayLD().setValue(cardsArray);
    }
}
