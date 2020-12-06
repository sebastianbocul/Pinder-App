package com.pinder.app.persistance;

import androidx.lifecycle.LiveData;

import com.pinder.app.models.MatchesObject;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public interface MatchesDao {
    LiveData<ArrayList<String>> getTags();

    LiveData<Resource<ArrayList<MatchesObject>>> getOryginalMatches();
}
