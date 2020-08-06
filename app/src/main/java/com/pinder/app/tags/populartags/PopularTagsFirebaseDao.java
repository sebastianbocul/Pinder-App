package com.pinder.app.tags.populartags;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

public interface PopularTagsFirebaseDao {
    MutableLiveData<List<PopularTagsObject>> getAllPopularTags();
}
