package com.pinder.app.Tags.PopularTags;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PopularTagsFragmentViewModel extends AndroidViewModel {
    private PopularTagsRepository popularTagsRepository;
    private LiveData<List<PopularTagsObject>> allPopularTags;

    public PopularTagsFragmentViewModel(@NonNull Application application) {
        super(application);
        popularTagsRepository = new PopularTagsRepository().getInstance();
        allPopularTags = popularTagsRepository.getAllPopularTags();
    }

    public LiveData<List<PopularTagsObject>> getAllPopularTags() {
        return allPopularTags;
    }


}
