package com.pinder.app.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.repository.SettingsRepository;
import com.pinder.app.util.Resource;

public class SettingsViewModel extends ViewModel {
    private LiveData<String> date;
    private LiveData<Boolean> showMyLocation;
    private LiveData<Boolean> sortByDistance;
    private SettingsRepository settingsRepository;

    @ViewModelInject
    public SettingsViewModel(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public void updateMyDb(Boolean dateValid, int logoutFlag) {
        settingsRepository.updateMyDb(dateValid,logoutFlag);
    }

    public LiveData<String> getDate() {
        date = settingsRepository.getDate();
        return date;
    }

    public LiveData<Boolean> getSortByDistance() {
        sortByDistance = settingsRepository.getSortByDistance();
        return sortByDistance;
    }

    public LiveData<Boolean> getShowMyLocation() {
        showMyLocation = settingsRepository.getShowMyLocation();
        return showMyLocation;
    }

    public void setDate(String date) {
        settingsRepository.setDate(date);
    }

    public void setSortByDistance(Boolean bool, SharedPreferences prefs) {
        prefs.edit().putString("sortByDistance", bool.toString()).apply();
        settingsRepository.setSortByDistance(bool);
    }

    public void setShowMyLocation(Boolean bool) {
        settingsRepository.setShowMyLocation(bool);
    }

    public void deleteWithRxJava(String userId) {
        settingsRepository.deleteWithRxJava(userId);
    }

    public void restartMatches() {
        settingsRepository.restartMatches();
    }

    public MutableLiveData<Resource<Integer>> getLogoutLiveData() {
        return settingsRepository.getLogoutLiveData();
    }
}
