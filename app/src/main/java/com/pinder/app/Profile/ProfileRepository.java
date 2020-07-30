package com.pinder.app.Profile;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.pinder.app.Images.ImageAdapter;

public class ProfileRepository {
    public static ProfileRepository instance = null;
    private LiveData<String> name;
    private LiveData<String> description;

    public static synchronized ProfileRepository getInstance() {
        if (instance == null) {
            instance = new ProfileRepository();
        }
        return instance;
    }

    public LiveData<String> getName() {
        name = ProfileFirebase.getInstance().getName();
        return name;
    }

    public LiveData<String> getDescription() {
        description = ProfileFirebase.getInstance().getDescription();
        return description;
    }

    public void saveUserInformation(String nameEdt, String descriptionEdt) {
        ProfileFirebase.getInstance().saveUserInformation(nameEdt, descriptionEdt);
    }

    public void deleteImage(Context context) {
        ProfileFirebase.getInstance().deleteImage(context);
    }

    public void setImagePosition(int position) {
        ProfileFirebase.getInstance().setImagePosition(position);
    }

    public void loadImages(Context context) {
        ProfileFirebase.getInstance().loadImages(context);
    }

    public void setDefault(Context context) {
        ProfileFirebase.getInstance().setDefault(context);
    }

    public void addImage(Context context, Uri resultUri) {
        ProfileFirebase.getInstance().addImage(context, resultUri);
    }

    public LiveData<ImageAdapter> getAdapter() {
        return ProfileFirebase.getInstance().getAdapter();
    }
}
