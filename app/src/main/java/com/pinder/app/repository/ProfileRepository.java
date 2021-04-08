package com.pinder.app.repository;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pinder.app.persistance.ProfileFirebase;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public class ProfileRepository {
    private final ProfileFirebase profileFirebase;

    public ProfileRepository(ProfileFirebase profileFirebase) {
        this.profileFirebase = profileFirebase;
    }

    public LiveData<Resource<String>> getName() {
        return profileFirebase.getName();
    }

    public LiveData<Resource<String>> getDescription() {
        return profileFirebase.getDescription();
    }

    public LiveData<Resource<ArrayList<String>>> getImages() {
        return profileFirebase.getImages();
    }

    public void saveUserInformation(String nameEdt, String descriptionEdt) {
        profileFirebase.saveUserInformation(nameEdt, descriptionEdt);
    }

    public void deleteImage(Context context) {
        profileFirebase.deleteImage(context);
    }

    public void setImagePosition(int position) {
        profileFirebase.setImagePosition(position);
    }

    public void loadImages() {
        profileFirebase.loadImages();
    }

    public void setDefault(Context context) {
        profileFirebase.setDefault(context);
    }

    public void addImage(Context context, Uri resultUri) {
        profileFirebase.addImage(context, resultUri);
    }

    public MutableLiveData<Boolean> getShowProgressBar() {
        return profileFirebase.getShowProgressBar();
    }
}
