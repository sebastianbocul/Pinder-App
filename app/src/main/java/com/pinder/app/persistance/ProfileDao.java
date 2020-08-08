package com.pinder.app.persistance;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;

public interface ProfileDao {
    void loadImages();

    LiveData<String> getName();

    LiveData<String> getDescription();

    LiveData<ArrayList> getImages();

    void setImagePosition(int position);

    void saveUserInformation(String nameEdt, String descriptionEdt);

    void deleteImage(Context context);

    void setDefault(Context context);

    void addImage(Context context, Uri resultUri);
}
