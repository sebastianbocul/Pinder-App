package com.pinder.app.Matches;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;

public interface MatchesDao {
    LiveData<ArrayList<MatchesObject>> getMatches();
    LiveData<ArrayList<String>> getTags();
}
