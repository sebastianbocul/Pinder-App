package com.pinder.app.Tags.PopularTags;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PopularTagsRepository {
    private static LiveData<List<PopularTagsObject>> allPopularTags;
    private static PopularTagsRepository instance = null;

    public static synchronized PopularTagsRepository getInstance() {
        if (instance == null) {
            Log.d("PopularTagsFragment", "Repository getInstance: ");
            PopularTagsFirebase popularTagsFirebase = new PopularTagsFirebase().getInstance();
            allPopularTags = popularTagsFirebase.getAllPopularTags();
            instance = new PopularTagsRepository();
        }
        return instance;
    }

    public PopularTagsRepository() {
    }

    public LiveData<List<PopularTagsObject>> getAllPopularTags() {
        return allPopularTags;
    }
}
