package com.pinder.app.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.models.Card;
import com.pinder.app.repository.MainRepository;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {
    MainRepository mainRepository;

    MutableLiveData<Resource<ArrayList<Card>>> rowItemsLD = new MutableLiveData<>();
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
        Log.d("MainFragment", "getTags viewModel " + myTagsAdapterLD.getValue());
        return myTagsAdapterLD;
    }

    public MutableLiveData<com.pinder.app.util.Resource<ArrayList<Card>>> getRowItemsLD() {
        rowItemsLD = mainRepository.getRowItemsLD();
        return rowItemsLD;
    }

    public void updateMyTagsAndSortBydDist() {
        mainRepository.updateMyTagsAndSortBydDist();
    }

    public void isConnectionMatch(Card obj, Context con) {
        mainRepository.isConnectionMatch(obj, con);
    }

    public void removeFirstObjectInAdapter() {
        Resource<ArrayList<Card>> rowItems = this.rowItemsLD.getValue();
        if (rowItems.data.size() != 0) {
            rowItems.data.remove(0);
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
