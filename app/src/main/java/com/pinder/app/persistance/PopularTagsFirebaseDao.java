package com.pinder.app.persistance;

import androidx.lifecycle.MutableLiveData;

import com.pinder.app.models.PopularTagsObject;
import com.pinder.app.util.Resource;

import java.util.List;

public interface PopularTagsFirebaseDao {
    MutableLiveData<Resource<List<PopularTagsObject>>> getAllPopularTags();
}
