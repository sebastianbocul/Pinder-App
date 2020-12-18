package com.pinder.app.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.pinder.app.cache.MatchesCache;
import com.pinder.app.models.MatchesObject;
import com.pinder.app.persistance.MatchesFirebase;
import com.pinder.app.util.AppExecutors;
import com.pinder.app.util.ConstantNetworkBoundResource;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public class MatchesRepository {
    private static final String TAG = "MatchesRepository";

    private MatchesCache matchesCache;
    private MatchesFirebase matchesFirebase;

    public MatchesRepository(MatchesFirebase matchesFirebase,MatchesCache matchesCache) {
        this.matchesFirebase=matchesFirebase;
        this.matchesCache = matchesCache;
    }

    public LiveData<Resource<ArrayList<MatchesObject>>> getOryginalMatches() {
        return matchesFirebase.getOryginalMatches();
    }

    public LiveData<Resource<ArrayList<String>>> getTags() {
        return new ConstantNetworkBoundResource<ArrayList<String>, ArrayList<String>>() {
            @Override
            protected void saveFirebaseResult(@NonNull ArrayList<String> item) {
                Log.d(TAG, "saveFirebaseResult TAGS: " + item.toString());
                matchesCache.saveTags(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable ArrayList<String> data) {
                Log.d(TAG, "shouldFetch TAGS: true");
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ArrayList<String>> loadFromDb() {
                Log.d(TAG, "loadFromDb TAGS: ");
                return matchesCache.getTags();
            }

            @NonNull
            @Override
            protected LiveData<Resource<ArrayList<String>>> createFirebaseCall() {
                Log.d(TAG, "createFirebaseCall TAGS: ");
                return matchesFirebase.getTags();
            }
        }.getAsLiveData();
    }

    public String getMyImageUrl() {
        return matchesFirebase.getMyImageUrl();
    }

    public LiveData<Resource<ArrayList<MatchesObject>>> getMatches() {
        return new ConstantNetworkBoundResource<ArrayList<MatchesObject>, ArrayList<MatchesObject>>(){
            @Override
            protected void saveFirebaseResult(@NonNull ArrayList<MatchesObject> item) {
                Log.d(TAG, "saveFirebaseResult: " + item.toString());
                matchesCache.saveMatches(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable ArrayList<MatchesObject> data) {
                Log.d(TAG, "shouldFetch: true");
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ArrayList<MatchesObject>> loadFromDb() {
                Log.d(TAG, "loadFromDb: ");
                return matchesCache.getMatches();
            }

            @NonNull
            @Override
            protected LiveData<Resource<ArrayList<MatchesObject>>> createFirebaseCall() {
                Log.d(TAG, "createFirebaseCall: ");
                return matchesFirebase.getOryginalMatches();
            }
        }.getAsLiveData();
    }
}
