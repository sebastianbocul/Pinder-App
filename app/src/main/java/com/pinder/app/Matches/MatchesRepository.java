package com.pinder.app.Matches;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;

public class MatchesRepository {
    public MatchesRepository instance = null;

    MatchesRepository getInstance() {
        if (instance == null) {
            instance = new MatchesRepository();
        }
        return instance;
    }

    public LiveData<ArrayList<MatchesObject>> getOryginalMatches() {
        return MatchesFirebase.getInstance().getOryginalMatches();
    }

    public LiveData<ArrayList<MatchesObject>> getResultMatches() {
        return MatchesFirebase.getInstance().getResultMatches();
    }

    public LiveData<ArrayList<String>> getTags() {
        return MatchesFirebase.getInstance().getTags();
    }
}
