package com.pinder.app.Home;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    MainRepository mainRepository;
    MutableLiveData<ArrayList<cards>> rowItemsLD = new MutableLiveData<>();
    public MainViewModel(@NonNull Application application) {
        super(application);
        mainRepository = MainRepository.getInstance(application);
    }
//    public MainViewModel() {
//        mainRepository = MainRepository.getInstance(context);
//    }

    public MutableLiveData<Double> getMyLatitude() {
        return mainRepository.getMyLatitude();
    }

    public MutableLiveData<Double> getMyLongitude() {
        return mainRepository.getMyLongitude();
    }

    public MutableLiveData<ArrayList<cards>> getRowItemsLD() {
        rowItemsLD = mainRepository.getRowItemsLD();
        return rowItemsLD;
    }

    public void getUsersFromDb(){
        mainRepository.getUsersFromDb();
    }


}
