package com.pinder.app.viewmodels;

import android.content.Context;
import android.net.Uri;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.repository.ProfileRepository;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public class ProfileViewModel extends ViewModel {
    private final ProfileRepository profileRepository;

    @ViewModelInject
    public ProfileViewModel(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public LiveData<Resource<String>> getName() {
        return profileRepository.getName();
    }

    public LiveData<Resource<String>> getDescription() {
        return profileRepository.getDescription();
    }

    public LiveData<Resource<ArrayList<String>>> getImages() {
        return profileRepository.getImages();
    }

    public void saveUserInformation(String nameEdt, String descriptionEdt) {
        if (nameEdt.length() != 0 || descriptionEdt.length() != 0) {
            profileRepository.saveUserInformation(nameEdt, descriptionEdt);
        }
    }

    public void deleteImage(Context context) {
        profileRepository.deleteImage(context);
    }

    public void setImagePosition(int position) {
        profileRepository.setImagePosition(position);
    }

    public void loadImages() {
        profileRepository.loadImages();
    }

    public void setDefault(Context context) {
        profileRepository.setDefault(context);
    }

    public void addImage(Context context, Uri resultUri) {
        profileRepository.addImage(context, resultUri);
    }

    public MutableLiveData<Boolean> getShowProgressBar() {
        return profileRepository.getShowProgressBar();
    }
}
