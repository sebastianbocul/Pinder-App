package com.pinder.app.viewmodels;

import android.content.SharedPreferences;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.repository.SettingsRepository;
import com.pinder.app.util.Resource;

public class SettingsViewModel extends ViewModel {
    private final SettingsRepository settingsRepository;

    @ViewModelInject
    public SettingsViewModel(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public void updateMyDb(Boolean dateValid, int logoutFlag) {
        settingsRepository.updateMyDb(dateValid, logoutFlag);
    }

    public LiveData<Resource<String>> getDate() {
        return settingsRepository.getDate();
    }

    public void setDate(String date) {
        settingsRepository.setDate(date);
    }

    public LiveData<Resource<Boolean>> getSortByDistance() {
        return settingsRepository.getSortByDistance();
    }

    public LiveData<Resource<Boolean>> getShowMyLocation() {
        return settingsRepository.getShowMyLocation();
    }

    public void setShowMyLocation(Boolean bool) {
        settingsRepository.setShowMyLocation(bool);
    }

    public void setSortByDistance(Boolean bool, SharedPreferences prefs) {
        prefs.edit().putString("sortByDistance", bool.toString()).apply();
        settingsRepository.setSortByDistance(bool);
    }

    public void deleteWithRxJava(String userId) {
        settingsRepository.deleteWithRxJava(userId);
    }

    public void restartMatches() {
        settingsRepository.restartMatches();
    }

    public LiveData<Resource<Integer>> getLogoutLiveData() {
        return settingsRepository.getLogoutLiveData();
    }
}
