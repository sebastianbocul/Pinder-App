package com.pinder.app.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.pinder.app.models.MatchesObject;
import com.pinder.app.persistance.MatchesFirebase;
import com.pinder.app.util.AppExecutors;
import com.pinder.app.util.NetworkBoundResource;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public class MatchesRepository {
    public static MatchesRepository instance = null;

    public MatchesRepository getInstance() {
        if (instance == null) {
            instance = new MatchesRepository();
        }
        return instance;
    }

    public LiveData<Resource<ArrayList<MatchesObject>>> getOryginalMatches() {
        return MatchesFirebase.getInstance().getOryginalMatches();
    }

    public LiveData<ArrayList<String>> getTags() {
        return MatchesFirebase.getInstance().getTags();
    }

    public String getMyImageUrl() {
        return MatchesFirebase.getInstance().getMyImageUrl();
    }

    public LiveData<Resource<ArrayList<MatchesObject>>> getMatches(final String query, final int pageNumber) {
        return new NetworkBoundResource<ArrayList<MatchesObject>, ArrayList<MatchesObject>>(AppExecutors.getInstance()){
            @Override
            protected void saveFirebaseResult(@NonNull ArrayList<MatchesObject> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable ArrayList<MatchesObject> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ArrayList<MatchesObject>> loadFromDb() {
                return null;
            }

            @NonNull
            @Override
            protected LiveData<Resource<ArrayList<MatchesObject>>> createFirebaseCall() {
                return MatchesFirebase.getInstance().getOryginalMatches();
            }
        }.getAsLiveData();
    }
}
