package com.pinder.app.viewmodels;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.models.TagsObject;
import com.pinder.app.repository.TagsRepository;

import java.util.List;

public class TagsViewModel extends ViewModel {
    private MutableLiveData<List<TagsObject>> tagList = new MutableLiveData<List<TagsObject>>();
    private TagsRepository tagsRepository;
    public int REQUEST_MODE = 1;
    public int position;

    @ViewModelInject
    public TagsViewModel(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    public MutableLiveData<List<TagsObject>> getAllTags() {
        return tagsRepository.getAllTags();
    }

    public void removeTag(TagsObject tag) {
        tagsRepository.deleteTag(tag);
    }

    public void addTag(TagsObject tag) {
        tagsRepository.addTag(tag);
    }
}
