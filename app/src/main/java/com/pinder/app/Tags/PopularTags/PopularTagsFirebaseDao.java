package com.pinder.app.Tags.PopularTags;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

public interface PopularTagsFirebaseDao {
    MutableLiveData<List<PopularTagsObject>> getAllPopularTags();
}
