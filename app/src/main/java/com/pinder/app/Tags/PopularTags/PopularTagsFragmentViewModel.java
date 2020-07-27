package com.pinder.app.Tags.PopularTags;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PopularTagsFragmentViewModel extends AndroidViewModel {
    private PopularTagsRepository popularTagsRepository;
    private LiveData<List<PopularTagsObject>> allPopularTags;

    public PopularTagsFragmentViewModel(@NonNull Application application) {
        super(application);
        Log.d("PopularTagsMVVM", "VM PopularTagsFragmentViewModelConstructor: ");
        popularTagsRepository = new PopularTagsRepository().getInstance();
        allPopularTags = popularTagsRepository.getAllPopularTags();
    }

    public LiveData<List<PopularTagsObject>> getAllPopularTags() {
        Log.d("PopularTagsMVVM", "VM getAllPopularTags: ");
        allPopularTags = Transformations.map(allPopularTags, id -> sortCollection(id));
        return allPopularTags;
    }

    public List<PopularTagsObject> sortCollection(List<PopularTagsObject> popularTagsList) {
        Log.d("PopularTagsMVVM", "VM sortCollection: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(popularTagsList, Comparator.comparing(PopularTagsObject::getTagPopularity).reversed());
        } else {
            Collections.sort(popularTagsList, new Comparator<PopularTagsObject>() {
                public int compare(PopularTagsObject o1, PopularTagsObject o2) {
                    if (o1.getTagPopularity() == o2.getTagPopularity())
                        return 0;
                    return o1.getTagPopularity() < o2.getTagPopularity() ? -1 : 1;
                }
            });
            Collections.reverse(popularTagsList);
        }
        return popularTagsList;
    }
}
