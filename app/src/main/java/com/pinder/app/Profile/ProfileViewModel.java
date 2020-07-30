package com.pinder.app.Profile;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.Images.ImageAdapter;

public class ProfileViewModel extends ViewModel {
    ProfileRepository profileRepository;
    private LiveData<String> name;
    private LiveData<String> description;

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
        profileRepository.saveUserInformation(nameEdt, descriptionEdt);
    }

    public void deleteImage(Context context) {
        profileRepository.getInstance().deleteImage(context);
    }

    public void setImagePosition(int position) {
        profileRepository.getInstance().setImagePosition(position);
    }

    public void loadImages(Context context) {
        profileRepository.getInstance().loadImages(context);
    }

    public void setDefault(Context context) {
        profileRepository.getInstance().setDefault(context);
    }

    public void addImage(Context context, Uri resultUri) {
        profileRepository.getInstance().addImage(context, resultUri);
    }

    public LiveData<ImageAdapter> getAdapter() {
        return profileRepository.getInstance().getAdapter();
    }
}
