package com.pinder.app.viewmodels;

import android.os.Build;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.pinder.app.models.PopularTagsObject;
import com.pinder.app.repository.PopularTagsRepository;
import com.pinder.app.util.Resource;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PopularTagsViewModel extends ViewModel {
    private final PopularTagsRepository popularTagsRepository;

    @ViewModelInject
    public PopularTagsViewModel(PopularTagsRepository popularTagsRepository) {
        this.popularTagsRepository = popularTagsRepository;
    }

    public LiveData<Resource<List<PopularTagsObject>>> getAllPopularTags() {
        MediatorLiveData<Resource<List<PopularTagsObject>>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(popularTagsRepository.getAllPopularTags(), new Observer<Resource<List<PopularTagsObject>>>() {
            @Override
            public void onChanged(Resource<List<PopularTagsObject>> o) {
                if (o != null && (o.status == Resource.Status.SUCCESS || o.status== Resource.Status.LOADING) && o.data!=null) {
                    List<PopularTagsObject> list = o.data;
                    mediatorLiveData.postValue(Resource.success(sortCollection(list)));
                } else {
                    mediatorLiveData.postValue(o);
                }
            }
        });
        return mediatorLiveData;
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
