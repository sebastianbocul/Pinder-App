package com.pinder.app.repository;

import androidx.lifecycle.LiveData;

import com.pinder.app.models.PopularTagsObject;
import com.pinder.app.persistance.PopularTagsFirebase;

import java.util.List;

public class PopularTagsRepository {
    public PopularTagsFirebase popularTagsFirebase;

    public PopularTagsRepository(PopularTagsFirebase popularTagsFirebase) {
        this.popularTagsFirebase = popularTagsFirebase;
    }

    public LiveData<List<PopularTagsObject>> getAllPopularTags() {
        return popularTagsFirebase.getAllPopularTags();
    }
}
