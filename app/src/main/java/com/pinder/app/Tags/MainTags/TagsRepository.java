package com.pinder.app.Tags.MainTags;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class TagsRepository implements TagsFirebaseDao {
    private MutableLiveData<List<TagsObject>> tagList = new MutableLiveData<List<TagsObject>>();
    private static TagsRepository instance = null;

    public static synchronized TagsRepository getInstance(){
        if(instance==null){
            instance=new TagsRepository();
        }
        return instance;
    }

    @Override
    public MutableLiveData<List<TagsObject>> getAllTags() {
        tagList=TagsFirebase.getInstance().getAllTags();
        return tagList;
    }

    @Override
    public TagsObject deleteTag(int position) {

        return null;
    }

    @Override
    public TagsObject addTag() {
        return null;
    }
}
