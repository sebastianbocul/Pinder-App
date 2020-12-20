package com.pinder.app.viewmodels;

import android.content.Context;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.models.Card;
import com.pinder.app.repository.MainRepository;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    MainRepository mainRepository;
    MutableLiveData<Resource<ArrayList<Card>>> cardsArrayLD = new MutableLiveData<>();
    MutableLiveData<ArrayList<String>> myTagsAdapterLD = new MutableLiveData<>();

    @ViewModelInject
    public MainViewModel(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
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

    public LiveData<Resource<ArrayList<Card>>> getCardsArrayLD() {
        cardsArrayLD = (MutableLiveData<Resource<ArrayList<Card>>>) mainRepository.getCardsArrayLD();
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
