package com.pinder.app.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.models.Card;
import com.pinder.app.repository.MainRepository;

import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {
    MainRepository mainRepository;

    MutableLiveData<ArrayList<Card>> rowItemsLD = new MutableLiveData<>();
    MutableLiveData<ArrayList<String>> myTagsAdapterLD = new MutableLiveData<>();
    public MainViewModel(@NonNull Application application) {
        super(application);
        mainRepository = MainRepository.getInstance(application);
    }

//    ToTestConstructor
//    public MainViewModel(){}

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
        ArrayList<Card> rowItems = this.rowItemsLD.getValue();
        if (rowItems.size() != 0) {
            rowItems.remove(0);
            this.rowItemsLD.postValue(rowItems);
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
