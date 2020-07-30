package com.pinder.app.Profile;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.pinder.app.Images.ImageAdapter;

public interface ProfileDao {
    void loadImages(Context context);

    LiveData<String> getName();

    LiveData<String> getDescription();

    void setImagePosition(int position);

    void saveUserInformation(String nameEdt, String descriptionEdt);

    void deleteImage(Context context);

    LiveData<ImageAdapter> getAdapter();

    void setDefault(Context context);

    void addImage(Context context, Uri resultUri);
}
