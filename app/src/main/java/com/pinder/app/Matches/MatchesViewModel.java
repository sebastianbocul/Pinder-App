package com.pinder.app.Matches;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MatchesViewModel extends ViewModel {
    MatchesRepository matchesRepository;
    LiveData<ArrayList<MatchesObject>> resultMatchesLiveData = new MutableLiveData<ArrayList<MatchesObject>>();
    LiveData<ArrayList<MatchesObject>> oryginalMatchesLiveData = new MutableLiveData<ArrayList<MatchesObject>>();

    public MatchesViewModel() {
        matchesRepository = new MatchesRepository();
    }

    public LiveData<ArrayList<MatchesObject>> getOryginalMatches() {
        oryginalMatchesLiveData = matchesRepository.getInstance().getOryginalMatches();
        return oryginalMatchesLiveData;
    }

    public LiveData<ArrayList<MatchesObject>> getResultMatches() {
        resultMatchesLiveData = matchesRepository.getInstance().getResultMatches();
        return resultMatchesLiveData;
    }

    public LiveData<ArrayList<String>> getTags() {
        return matchesRepository.getInstance().getTags();
    }
}
