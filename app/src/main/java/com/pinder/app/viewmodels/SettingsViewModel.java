package com.pinder.app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pinder.app.persistance.SettingsFirebaseDao;
import com.pinder.app.repository.SettingsRepository;

public class SettingsViewModel extends AndroidViewModel implements SettingsFirebaseDao {
    private LiveData<String> date;
    private LiveData<Boolean> showMyLocation;
    private LiveData<Boolean> sortByDistance;
    private SettingsRepository settingsRepository = null;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        settingsRepository = SettingsRepository.getInstance();
    }

    @Override
    public void updateMyDb(Boolean dateValid) {
        settingsRepository.updateMyDb(dateValid);
    }

    @Override
    public LiveData<String> getDate() {
        date = settingsRepository.getDate();
        return date;
    }

    @Override
    public LiveData<Boolean> getSortByDistance() {
        sortByDistance = settingsRepository.getSortByDistance();
        return sortByDistance;
    }

    @Override
    public LiveData<Boolean> getShowMyLocation() {
        showMyLocation = settingsRepository.getShowMyLocation();
        return showMyLocation;
    }

    @Override
    public void setDate(String date) {
        settingsRepository.setDate(date);
    }

    @Override
    public void setSortByDistance(Boolean bool) {
        settingsRepository.setSortByDistance(bool);
    }

    @Override
    public void setShowMyLocation(Boolean bool) {
        settingsRepository.setShowMyLocation(bool);
    }
}
