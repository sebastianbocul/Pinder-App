package com.pinder.app.repository;
import androidx.lifecycle.LiveData;
import com.pinder.app.models.PopularTagsObject;
import com.pinder.app.persistance.PopularTagsFirebase;
import com.pinder.app.util.Resource;
import java.util.List;

public class PopularTagsRepository {
    public PopularTagsFirebase popularTagsFirebase;
    private static final String TAG = "PopularTagsRepository";
    public PopularTagsRepository(PopularTagsFirebase popularTagsFirebase) {
        this.popularTagsFirebase = popularTagsFirebase;
    }

    public LiveData<Resource<List<PopularTagsObject>>> getAllPopularTags() {
        return popularTagsFirebase.getAllPopularTags();
    }
}
