package com.pinder.app.adapters;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.pinder.app.models.Card;
import com.pinder.app.repository.MainRepository;

import java.util.ArrayList;

public class CardsViewModel extends AndroidViewModel {
    MainRepository mainRepository;
    MutableLiveData<ArrayList<Card>> rowItemsLD = new MutableLiveData<>();
    MutableLiveData<ArrayList<String>> myTagsAdapterLD = new MutableLiveData<>();
    public CardsViewModel(@NonNull Application application) {
        super(application);
        mainRepository = MainRepository.getInstance(application);
    }
//    public CardsViewModel() {
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


    public MutableLiveData<ArrayList<Card>> getRowItemsLD() {
        rowItemsLD = mainRepository.getRowItemsLD();
        return rowItemsLD;
    }

    public void getUsersFromDb() {
        mainRepository.getUsersFromDb();
    }

    public void isConnectionMatch(Card obj, Context con) {
        mainRepository.isConnectionMatch(obj, con);
    }

    public void removeFirstObjectInAdapter() {
        ArrayList<Card> rowItems = rowItemsLD.getValue();
        if (rowItems.size() != 0) {
            rowItems.remove(0);
            rowItemsLD.postValue(rowItems);
        }
    }

    public void updateToken(){
        mainRepository.updateToken();
    }

    public void checkUserStatus(Context context){
        mainRepository.checkUserStatus(context);
    }

    public void onLeftCardExit(Card dataObject){
        mainRepository.onLeftCardExit(dataObject);
    }
}
