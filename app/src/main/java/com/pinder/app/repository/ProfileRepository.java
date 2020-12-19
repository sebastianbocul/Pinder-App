package com.pinder.app.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pinder.app.cache.ProfileCache;
import com.pinder.app.persistance.ProfileFirebase;
import com.pinder.app.util.ConstantNetworkBoundResource;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public class ProfileRepository {
    private ProfileFirebase profileFirebase;
    private ProfileCache profileCache;

    public ProfileRepository(ProfileFirebase profileFirebase, ProfileCache profileCache) {
        this.profileFirebase = profileFirebase;
        this.profileCache = profileCache;
    }

    public LiveData<Resource<String>> getName() {
        return new ConstantNetworkBoundResource<String, String>() {
            @Override
            protected void saveFirebaseResult(@NonNull String item) {
                profileCache.setName(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable String data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<String> loadFromDb() {
                return profileCache.getName();
            }

            @NonNull
            @Override
            protected LiveData<Resource<String>> createFirebaseCall() {
                return profileFirebase.getName();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<String>> getDescription() {
        return new ConstantNetworkBoundResource<String, String>() {
            @Override
            protected void saveFirebaseResult(@NonNull String item) {
                profileCache.setDescription(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable String data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<String> loadFromDb() {
                return profileCache.getDescription();
            }

            @NonNull
            @Override
            protected LiveData<Resource<String>> createFirebaseCall() {
                return profileFirebase.getDescription();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ArrayList<String>>> getImages() {
        return new ConstantNetworkBoundResource<ArrayList<String>, ArrayList<String>>() {
            @Override
            protected void saveFirebaseResult(@NonNull ArrayList<String> item) {
                profileCache.setImages(item);
            }
            @Override
            protected boolean shouldFetch(@Nullable ArrayList<String> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ArrayList<String>> loadFromDb() {
                return profileCache.getImages();
            }

            @NonNull
            @Override
            protected LiveData<Resource<ArrayList<String>>> createFirebaseCall() {
                return profileFirebase.getImages();
            }
        }.getAsLiveData();
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
