package com.pinder.app.Matches;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MatchesViewModel extends ViewModel {
    MatchesRepository matchesRepository;

    public MatchesViewModel(){
        matchesRepository = new MatchesRepository();
    }



    public LiveData<ArrayList<String>> getTags() {
        return matchesRepository.getInstance().getTags();
    }
}
