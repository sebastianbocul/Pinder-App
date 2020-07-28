package com.pinder.app.Tags.MainTags;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

public interface TagsFirebaseDao {
    MutableLiveData<List<TagsObject>> getAllTags();

    void deleteTag(TagsObject tag);

    void addTag(TagsObject tag);
}
