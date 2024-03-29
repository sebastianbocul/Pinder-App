package com.pinder.app.persistance;

import androidx.lifecycle.MutableLiveData;

import com.pinder.app.models.TagsObject;
import com.pinder.app.util.Resource;

import java.util.List;

public interface TagsFirebaseDao {
    MutableLiveData<Resource<List<TagsObject>>> getAllTags();

    void deleteTag(TagsObject tag);

    void addTag(TagsObject tag);
}
