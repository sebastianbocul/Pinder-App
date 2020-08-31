package com.pinder.app.viewmodels;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.persistance.ProfileFirebase;
import com.pinder.app.repository.ProfileRepository;

import java.util.ArrayList;

public class ProfileViewModel extends ViewModel {
    ProfileRepository profileRepository;
    private LiveData<String> name;
    private LiveData<String> description;
    private LiveData<ArrayList> images;

    public ProfileViewModel() {
        profileRepository = ProfileRepository.getInstance();
    }

    public LiveData<String> getName() {
        name = profileRepository.getName();
        return name;
    }

    public LiveData<String> getDescription() {
        description = profileRepository.getDescription();
        return description;
    }

    public void saveUserInformation(String nameEdt, String descriptionEdt) {
        if (nameEdt.length() != 0 || descriptionEdt.length()!=0) {
            profileRepository.saveUserInformation(nameEdt, descriptionEdt);
        }
    }

    public void deleteImage(Context context) {
        profileRepository.getInstance().deleteImage(context);
    }

    public void setImagePosition(int position) {
        profileRepository.getInstance().setImagePosition(position);
    }

    public void loadImages() {
        profileRepository.getInstance().loadImages();
    }

    public void setDefault(Context context) {
        profileRepository.getInstance().setDefault(context);
    }

    public void addImage(Context context, Uri resultUri) {
        profileRepository.getInstance().addImage(context, resultUri);
    }

    public LiveData<ArrayList> getImages() {
        images = profileRepository.getInstance().getImages();
        return images;
    }

    public MutableLiveData<Boolean> getShowProgressBar() {
        return profileRepository.getInstance().getShowProgressBar();
    }

}
