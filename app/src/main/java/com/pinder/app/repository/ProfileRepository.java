package com.pinder.app.repository;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pinder.app.persistance.ProfileFirebase;

import java.util.ArrayList;

public class ProfileRepository {
    public static ProfileRepository instance = null;
    private LiveData<String> name;
    private LiveData<String> description;
    private LiveData<ArrayList> images;

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

    public LiveData<ArrayList> getImages() {
        images = ProfileFirebase.getInstance().getImages();
        return images;
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

    public void loadImages() {
        ProfileFirebase.getInstance().loadImages();
    }

    public void setDefault(Context context) {
        ProfileFirebase.getInstance().setDefault(context);
    }

    public void addImage(Context context, Uri resultUri) {
        ProfileFirebase.getInstance().addImage(context, resultUri);
    }

    public MutableLiveData<Boolean> getShowProgressBar() {
        return ProfileFirebase.getInstance().getShowProgressBar();
    }

}
