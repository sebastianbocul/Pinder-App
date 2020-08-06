package com.pinder.app.matches;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;

public interface MatchesDao {
    LiveData<ArrayList<String>> getTags();

    LiveData<ArrayList<MatchesObject>> getOryginalMatches();
}
