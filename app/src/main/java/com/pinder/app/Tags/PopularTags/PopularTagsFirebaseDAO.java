package com.pinder.app.Tags.PopularTags;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

public interface PopularTagsFirebaseDAO {
    MutableLiveData<List<PopularTagsObject>> getAllPopularTags();
}
