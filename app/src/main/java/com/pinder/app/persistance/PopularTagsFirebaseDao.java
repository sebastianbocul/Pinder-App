package com.pinder.app.persistance;

import androidx.lifecycle.MutableLiveData;

import com.pinder.app.models.PopularTagsObject;

import java.util.List;

public interface PopularTagsFirebaseDao {
    MutableLiveData<List<PopularTagsObject>> getAllPopularTags();
}
