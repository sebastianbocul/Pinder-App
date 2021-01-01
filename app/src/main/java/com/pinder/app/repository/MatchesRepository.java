package com.pinder.app.repository;
import androidx.lifecycle.LiveData;
import com.pinder.app.models.MatchesObject;
import com.pinder.app.persistance.MatchesFirebase;
import com.pinder.app.util.Resource;
import java.util.ArrayList;

public class MatchesRepository {
    private static final String TAG = "MatchesRepository";
    private MatchesFirebase matchesFirebase;

    public MatchesRepository(MatchesFirebase matchesFirebase) {
        this.matchesFirebase=matchesFirebase;
    }

    public LiveData<Resource<ArrayList<String>>> getTags() {
        return matchesFirebase.getTags();
    }

    public LiveData<Resource<ArrayList<MatchesObject>>> getMatches() {
        return matchesFirebase.getOryginalMatches();
    }
}
