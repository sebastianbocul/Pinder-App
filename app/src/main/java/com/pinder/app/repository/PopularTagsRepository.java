package com.pinder.app.repository;

import androidx.lifecycle.LiveData;

import com.pinder.app.models.PopularTagsObject;
import com.pinder.app.persistance.PopularTagsFirebase;

import java.util.List;

public class PopularTagsRepository {
    private static LiveData<List<PopularTagsObject>> allPopularTags;
    public static PopularTagsRepository instance = null;

    public static synchronized PopularTagsRepository getInstance() {
        if (instance == null) {
            instance = new PopularTagsRepository();
        }
        return instance;
    }

    public LiveData<List<PopularTagsObject>> getAllPopularTags() {
        allPopularTags = new PopularTagsFirebase().getInstance().getAllPopularTags();
        return allPopularTags;
    }
}
