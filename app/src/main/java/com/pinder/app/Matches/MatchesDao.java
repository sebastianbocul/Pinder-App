package com.pinder.app.Matches;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;

public interface MatchesDao {
    LiveData<ArrayList<String>> getTags();
    LiveData<ArrayList<MatchesObject>> getOryginalMatches();
    LiveData<ArrayList<MatchesObject>> getResultMatches();
}
