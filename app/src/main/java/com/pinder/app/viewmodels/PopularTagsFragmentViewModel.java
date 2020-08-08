package com.pinder.app.viewmodels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.pinder.app.models.PopularTagsObject;
import com.pinder.app.repository.PopularTagsRepository;

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
        allPopularTags = Transformations.map(allPopularTags, list -> sortCollection(list));
        return allPopularTags;
    }

    public static List<PopularTagsObject> sortCollection(List<PopularTagsObject> popularTagsList) {
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
