package com.pinder.app.repository;

import androidx.lifecycle.LiveData;

import com.pinder.app.models.MatchesObject;
import com.pinder.app.persistance.MatchesFirebase;

import java.util.ArrayList;

public class MatchesRepository {
    public static MatchesRepository instance = null;

    public MatchesRepository getInstance() {
        if (instance == null) {
            instance = new MatchesRepository();
        }
        return instance;
    }

    public LiveData<ArrayList<MatchesObject>> getOryginalMatches() {
        return MatchesFirebase.getInstance().getOryginalMatches();
    }

    public LiveData<ArrayList<String>> getTags() {
        return MatchesFirebase.getInstance().getTags();
    }
}
