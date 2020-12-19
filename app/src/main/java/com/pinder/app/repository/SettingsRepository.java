package com.pinder.app.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.pinder.app.cache.SettingsCache;
import com.pinder.app.persistance.SettingsFirebase;
import com.pinder.app.persistance.SettingsFirebaseDao;
import com.pinder.app.util.NetworkBoundResource;
import com.pinder.app.util.Resource;

public class SettingsRepository implements SettingsFirebaseDao {
    private SettingsFirebase settingsFirebase;
    private SettingsCache settingsCache;
    private static final String TAG = "SettingsRepository";

    public SettingsRepository(SettingsFirebase settingsFirebase, SettingsCache settingsCache) {
        this.settingsFirebase = settingsFirebase;
        this.settingsCache = settingsCache;
    }

    @Override
    public void updateMyDb(Boolean dateValid, int logoutFlag) {
        settingsFirebase.updateMyDb(dateValid, logoutFlag);
    }

    @Override
    public LiveData<Resource<String>> getDate() {
        return new NetworkBoundResource<String, String>() {
            @Override
            protected void saveFirebaseResult(@NonNull String item) {
                Log.d(TAG, "saveFirebaseResult: DATE " + item);
                settingsCache.setDate(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable String data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<String> loadFromDb() {
                return settingsCache.getDate();
            }

            @NonNull
            @Override
            protected LiveData<Resource<String>> createFirebaseCall() {
                return settingsFirebase.getDate();
            }
        }.getAsLiveData();
    }

    @Override
    public LiveData<Resource<Boolean>> getSortByDistance() {
        return new NetworkBoundResource<Boolean, Boolean>() {
            @Override
            protected void saveFirebaseResult(@NonNull Boolean item) {
                Log.d(TAG, "saveFirebaseResult SORT BY DISTN: " + item);
                settingsCache.setSortByDistance(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Boolean data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Boolean> loadFromDb() {
                return settingsCache.getSortByDistance();
            }

            @NonNull
            @Override
            protected LiveData<Resource<Boolean>> createFirebaseCall() {
                return settingsFirebase.getSortByDistance();
            }
        }.getAsLiveData();
    }

    @Override
    public LiveData<Resource<Boolean>> getShowMyLocation() {
        return new NetworkBoundResource<Boolean, Boolean>() {
            @Override
            protected void saveFirebaseResult(@NonNull Boolean item) {
                Log.d(TAG, "saveFirebaseResult: SHOW MY LOC " + item);
                settingsCache.setShowMyLocation(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Boolean data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Boolean> loadFromDb() {
                return settingsCache.getShowMyLocation();
            }

            @NonNull
            @Override
            protected LiveData<Resource<Boolean>> createFirebaseCall() {
                return settingsFirebase.getShowMyLocation();
            }
        }.getAsLiveData();
    }

    @Override
    public void setDate(String date) {
        settingsFirebase.setDate(date);
    }

    @Override
    public void setSortByDistance(Boolean bool) {
        settingsFirebase.setSortByDistance(bool);
    }

    @Override
    public void setShowMyLocation(Boolean bool) {
        settingsFirebase.setShowMyLocation(bool);
    }

    public void deleteWithRxJava(String userId) {
        settingsFirebase.deleteWithRxJava(userId);
    }

    public void restartMatches() {
        settingsFirebase.restartMatches();
    }

    public LiveData<Resource<Integer>> getLogoutLiveData() {
        MediatorLiveData<Resource<Integer>> logout = new MediatorLiveData<>();
        logout.addSource(settingsFirebase.getLogoutLiveData(), new Observer<Resource<Integer>>() {
                @Override
                public void onChanged(Resource<Integer> integerResource) {
                    if (integerResource.status == Resource.Status.SUCCESS) {
                        settingsCache.removeUserCache();
                    }
                    logout.postValue(integerResource);
                }
            });
        return logout;
    }
}
