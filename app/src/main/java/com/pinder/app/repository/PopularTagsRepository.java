package com.pinder.app.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.pinder.app.cache.PopularTagsCache;
import com.pinder.app.models.PopularTagsObject;
import com.pinder.app.persistance.PopularTagsFirebase;
import com.pinder.app.util.bound_resources.ConstantNetworkBoundResource;
import com.pinder.app.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class PopularTagsRepository {
    public PopularTagsFirebase popularTagsFirebase;
    public PopularTagsCache popularTagsCache;
    private static final String TAG = "PopularTagsRepository";
    public PopularTagsRepository(PopularTagsFirebase popularTagsFirebase, PopularTagsCache popularTagsCache) {
        this.popularTagsFirebase = popularTagsFirebase;
        this.popularTagsCache = popularTagsCache;
    }

    public LiveData<Resource<List<PopularTagsObject>>> getAllPopularTags() {
        return new ConstantNetworkBoundResource<List<PopularTagsObject>, List<PopularTagsObject>>() {
            @Override
            protected void saveFirebaseResult(@NonNull List<PopularTagsObject> item) {
                Log.d(TAG, "saveFirebaseResult: " +item);
                popularTagsCache.savePopularTags((ArrayList<PopularTagsObject>) item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<PopularTagsObject> data) {
                Log.d(TAG, "shouldFetch: " + data);
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<PopularTagsObject>> loadFromDb() {
                Log.d(TAG, "loadFromDb: ");
                return popularTagsCache.getPopularTags();
            }

            @NonNull
            @Override
            protected LiveData<Resource<List<PopularTagsObject>>> createFirebaseCall() {
                Log.d(TAG, "createFirebaseCall: ");
                return popularTagsFirebase.getAllPopularTags();
            }
        }.getAsLiveData();
    }
}
