package com.pinder.app.viewmodels;

import android.os.Build;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.pinder.app.models.PopularTagsObject;
import com.pinder.app.repository.PopularTagsRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PopularTagsViewModel extends ViewModel {
    private PopularTagsRepository popularTagsRepository;

    @ViewModelInject
    public PopularTagsViewModel(PopularTagsRepository popularTagsRepository) {
        this.popularTagsRepository = popularTagsRepository;
    }

    public LiveData<List<PopularTagsObject>> getAllPopularTags() {
        return Transformations.map(popularTagsRepository.getAllPopularTags(), PopularTagsViewModel::sortCollection);
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
