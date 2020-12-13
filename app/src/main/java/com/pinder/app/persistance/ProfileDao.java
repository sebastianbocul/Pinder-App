package com.pinder.app.persistance;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.pinder.app.util.Resource;

import java.util.ArrayList;

public interface ProfileDao {
    void loadImages();

    MutableLiveData<Resource<String>> getName();

    MutableLiveData<Resource<String>> getDescription();

    MutableLiveData<Resource<ArrayList<String>>> getImages();

    void setImagePosition(int position);

    void saveUserInformation(String nameEdt, String descriptionEdt);

    void deleteImage(Context context);

    void setDefault(Context context);

    void addImage(Context context, Uri resultUri);
}
