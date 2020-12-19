package com.pinder.app.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.pinder.app.cache.TagsCache;
import com.pinder.app.models.TagsObject;
import com.pinder.app.persistance.TagsFirebase;
import com.pinder.app.util.ConstantNetworkBoundResource;
import com.pinder.app.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class TagsRepository {
    private TagsFirebase tagsFirebase;
    private TagsCache tagsCache;
    private static final String TAG = "TagsRepository";

    public TagsRepository(TagsFirebase tagsFirebase, TagsCache tagsCache) {
        this.tagsFirebase = tagsFirebase;
        this.tagsCache = tagsCache;
    }

    public LiveData<Resource<List<TagsObject>>> getAllTags() {
        return new ConstantNetworkBoundResource<List<TagsObject>, List<TagsObject>>() {
            @Override
            protected void saveFirebaseResult(@NonNull List<TagsObject> item) {
                Log.d(TAG, "saveFirebaseResult: " + item);
                tagsCache.saveTags((ArrayList<TagsObject>) item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<TagsObject> data) {
                Log.d(TAG, "shouldFetch: " + data);
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<TagsObject>> loadFromDb() {
                Log.d(TAG, "loadFromDb: ");
                return tagsCache.getTags();
            }

            @NonNull
            @Override
            protected LiveData<Resource<List<TagsObject>>> createFirebaseCall() {
                Log.d(TAG, "createFirebaseCall: ");
                return tagsFirebase.getAllTags();
            }
        }.getAsLiveData();
    }

    public void deleteTag(TagsObject tag) {
        tagsFirebase.deleteTag(tag);
    }

    public void addTag(TagsObject tag) {
        tagsFirebase.addTag(tag);
    }
}
