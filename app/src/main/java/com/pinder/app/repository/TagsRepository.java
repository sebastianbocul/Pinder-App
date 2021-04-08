package com.pinder.app.repository;

import androidx.lifecycle.LiveData;

import com.pinder.app.models.TagsObject;
import com.pinder.app.persistance.TagsFirebase;
import com.pinder.app.util.Resource;

import java.util.List;

public class TagsRepository {
    private static final String TAG = "TagsRepository";
    private final TagsFirebase tagsFirebase;

    public TagsRepository(TagsFirebase tagsFirebase) {
        this.tagsFirebase = tagsFirebase;
    }

    public LiveData<Resource<List<TagsObject>>> getAllTags() {
        return tagsFirebase.getAllTags();
    }

    public void deleteTag(TagsObject tag) {
        tagsFirebase.deleteTag(tag);
    }

    public void addTag(TagsObject tag) {
        tagsFirebase.addTag(tag);
    }
}
