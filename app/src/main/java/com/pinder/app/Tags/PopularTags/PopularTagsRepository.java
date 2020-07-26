package com.pinder.app.Tags.PopularTags;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PopularTagsRepository {
    private static LiveData<List<PopularTagsObject>> allPopularTags;
    private static PopularTagsRepository instance = null;

    public static synchronized PopularTagsRepository getInstance() {
        if (instance == null) {
            Log.d("PopularTagsMVVM", "REP Repository getInstance: ");
            instance = new PopularTagsRepository();
        }
        return instance;
    }

    public LiveData<List<PopularTagsObject>> getAllPopularTags() {
        allPopularTags = new PopularTagsFirebase().getInstance().getAllPopularTags();
        Log.d("PopularTagsMVVM", "REP getAllPopularTags: ");
        return allPopularTags;
    }
}
