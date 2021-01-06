package com.pinder.app.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.models.Card;
import com.pinder.app.repository.MainRepository;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

import static com.pinder.app.util.SortingFunctions.sortCollectionByLikesMe;
import static com.pinder.app.util.SortingFunctions.sortCollectionByLikesMeThenDistance;

public class MainViewModel extends ViewModel {
    MainRepository mainRepository;
    MutableLiveData<Resource<ArrayList<Card>>> cardsArrayLD = new MutableLiveData<>();
    MediatorLiveData<Resource<ArrayList<Card>>> mediatorLiveData = new MediatorLiveData<>();
    private static final String TAG = "MainViewModel";

    @ViewModelInject
    public MainViewModel(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    public LiveData<Resource<ArrayList<Card>>> getCardsArrayLD() {
        mediatorLiveData = new MediatorLiveData<>();
        Log.d(TAG, "getCardsArrayLD: ");
        mediatorLiveData.addSource(mainRepository.getCardsArrayLD(), arrayListResource -> {
            ArrayList<Card> list = new ArrayList<>();
            if (arrayListResource.data != null) {
                list.addAll(arrayListResource.data);
            }
            Resource<ArrayList<Card>> cards = new Resource<>(arrayListResource.status, list, arrayListResource.message);
            Log.d(TAG, mainRepository.getSortByDistanceString());
            if (mainRepository.getSortByDistanceString().equals("true")) {
                if (cards != null && cards.data != null) {
                    cards.data = sortCollectionByLikesMeThenDistance(cards.data);
                }
            } else {
                if (cards != null && cards.data != null) {
                    cards.data = sortCollectionByLikesMe(cards.data);
                }
            }
            mediatorLiveData.postValue(cards);
        });
        return mediatorLiveData;
    }

    public void fetchDataOrUpdateLocationAndFetchData() {
        mainRepository.fetchDataOrUpdateLocationAndFetchData();
    }

    public void isConnectionMatch(Card obj, Context con) {
        mainRepository.isConnectionMatch(obj, con);
    }

    public void removeFirstObjectInAdapter() {
        Resource<ArrayList<Card>> cardsArray = this.mediatorLiveData.getValue();
        if (cardsArray != null && cardsArray.data != null && cardsArray.data.size() != 0) {
            mainRepository.removeCardFromArray(cardsArray.data.get(0));
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
