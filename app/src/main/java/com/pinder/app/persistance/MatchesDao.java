package com.pinder.app.persistance;

import androidx.lifecycle.LiveData;

import com.pinder.app.models.MatchesObject;

import java.util.ArrayList;

public interface MatchesDao {
    LiveData<ArrayList<String>> getTags();

    LiveData<ArrayList<MatchesObject>> getOryginalMatches();
}
