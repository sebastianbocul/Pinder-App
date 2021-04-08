package com.pinder.app.viewmodels;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.models.TagsObject;
import com.pinder.app.repository.TagsRepository;
import com.pinder.app.util.Resource;

import java.util.List;

public class TagsViewModel extends ViewModel {
    public int REQUEST_MODE = 1;
    public int position;
    private final TagsRepository tagsRepository;

    @ViewModelInject
    public TagsViewModel(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    public LiveData<Resource<List<TagsObject>>> getAllTags() {
        return tagsRepository.getAllTags();
    }

    public void removeTag(TagsObject tag) {
        tagsRepository.deleteTag(tag);
    }

    public void addTag(TagsObject tag) {
        tagsRepository.addTag(tag);
    }
}
