package com.pinder.app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pinder.app.persistance.SettingsFirebase;
import com.pinder.app.persistance.SettingsFirebaseDao;
import com.pinder.app.util.Resource;

public class SettingsRepository implements SettingsFirebaseDao {
    private LiveData<String> date;
    private LiveData<Boolean> showMyLocation;
    private LiveData<Boolean> sortByDistance;
    private SettingsFirebase settingsFirebase;

    public SettingsRepository(SettingsFirebase settingsFirebase) {
        this.settingsFirebase = settingsFirebase;
    }

    @Override
    public void updateMyDb(Boolean dateValid, int logoutFlag) {
        settingsFirebase.updateMyDb(dateValid, logoutFlag);
    }

    @Override
    public LiveData<String> getDate() {
        date = settingsFirebase.getDate();
        return date;
    }

    @Override
    public LiveData<Boolean> getSortByDistance() {
        sortByDistance = settingsFirebase.getSortByDistance();
        return sortByDistance;
    }

    @Override
    public LiveData<Boolean> getShowMyLocation() {
        showMyLocation = settingsFirebase.getShowMyLocation();
        return showMyLocation;
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

    public MutableLiveData<Resource<Integer>> getLogoutLiveData() {
        return settingsFirebase.getLogoutLiveData();
    }
}
