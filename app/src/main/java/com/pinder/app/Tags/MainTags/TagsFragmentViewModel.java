package com.pinder.app.Tags.MainTags;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.LegalInfo.PrivacyDialog;

import java.util.List;

public class TagsFragmentViewModel extends ViewModel {
    private MutableLiveData<List<TagsObject>> tagList = new MutableLiveData<List<TagsObject>>();
    private TagsRepository tagsRepository;

    public TagsFragmentViewModel() {
        tagsRepository = new TagsRepository().getInstance();
        tagList=tagsRepository.getAllTags();
    }

    public MutableLiveData<List<TagsObject>> getAllTags(){
        return tagList;
    }
}
