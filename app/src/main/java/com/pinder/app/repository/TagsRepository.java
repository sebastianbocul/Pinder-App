package com.pinder.app.repository;

import androidx.lifecycle.MutableLiveData;

import com.pinder.app.models.TagsObject;
import com.pinder.app.persistance.TagsFirebase;
import com.pinder.app.persistance.TagsFirebaseDao;

import java.util.List;

public class TagsRepository implements TagsFirebaseDao {
    private MutableLiveData<List<TagsObject>> tagList = new MutableLiveData<List<TagsObject>>();
    private TagsFirebase tagsFirebase;

    public TagsRepository(TagsFirebase tagsFirebase) {
        this.tagsFirebase = tagsFirebase;
    }

    @Override
    public MutableLiveData<List<TagsObject>> getAllTags() {
        tagList = tagsFirebase.getAllTags();
        return tagList;
    }

    @Override
    public void deleteTag(TagsObject tag) {
        tagsFirebase.deleteTag(tag);
    }

    @Override
    public void addTag(TagsObject tag) {
        tagsFirebase.addTag(tag);
    }
}
