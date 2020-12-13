package com.pinder.app.cache;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;

interface ProfileCacheDao {
    LiveData<String> getName();

    LiveData<String> getDescription();

    LiveData<ArrayList<String>> getImages();

    void setName(String name);

    void setDescription(String description);

    void setImages(ArrayList<String> imagesUrls);
}
