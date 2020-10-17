package com.pinder.app.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.pinder.app.models.Card;
import com.pinder.app.repository.MainRepository;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {
    MainRepository mainRepository;
    MutableLiveData<Resource<ArrayList<Card>>> cardsArrayLD = new MutableLiveData<>();
    MutableLiveData<ArrayList<String>> myTagsAdapterLD = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        mainRepository = MainRepository.getInstance(application);
    }
//    ToTestConstructor
//    public MainViewModel(){}

//    public MutableLiveData<Double> getMyLatitude() {
//        return mainRepository.getMyLatitude();
//    }
//
//    public MutableLiveData<Double> getMyLongitude() {
//        return mainRepository.getMyLongitude();
//    }

    public MutableLiveData<ArrayList<String>> getMyTagsAdapterLD() {
        myTagsAdapterLD = mainRepository.getMyTagsAdapterLD();
        return myTagsAdapterLD;
    }

    public MutableLiveData<com.pinder.app.util.Resource<ArrayList<Card>>> getCardsArrayLD() {
        cardsArrayLD = mainRepository.getCardsArrayLD();
        return cardsArrayLD;
    }

    public void fetchDataOrUpdateLocationAndFetchData() {
        mainRepository.fetchDataOrUpdateLocationAndFetchData();
    }
    public void isConnectionMatch(Card obj, Context con) {
        mainRepository.isConnectionMatch(obj, con);
    }

    public void removeFirstObjectInAdapter() {
        Resource<ArrayList<Card>> cardsArray = this.cardsArrayLD.getValue();
        if (cardsArray.data.size() != 0) {
            cardsArray.data.remove(0);
            if (cardsArray.data.size() == 0) {
                cardsArray = Resource.emptydata(cardsArray.data);
            }
            this.cardsArrayLD.postValue(cardsArray);
        }
    }

    public void updateToken() {
        mainRepository.updateToken();
    }

    public void checkUserStatus(Context context) {
        mainRepository.checkUserStatus(context);
    }

    public void onLeftCardExit(Card dataObject) {
        mainRepository.onLeftCardExit(dataObject);
    }
}
