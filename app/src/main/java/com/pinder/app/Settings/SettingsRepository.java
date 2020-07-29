package com.pinder.app.Settings;

import androidx.lifecycle.LiveData;

public class SettingsRepository implements SettingsFirebaseDao {
    static SettingsRepository instance = null;
    private LiveData<String> date;
    private LiveData<Boolean> showMyLocation;
    private LiveData<Boolean> sortByDistance;

    public static synchronized SettingsRepository getInstance() {
        if (instance == null) {
            instance = new SettingsRepository();
        }
        return instance;
    }

    @Override
    public void updateMyDb(Boolean dateValid) {
        SettingsFirebase.getInstance().updateMyDb(dateValid);
    }

    @Override
    public LiveData<String> getDate() {
        date = SettingsFirebase.getInstance().getDate();
        return date;
    }

    @Override
    public LiveData<Boolean> getSortByDistance() {
        sortByDistance = SettingsFirebase.getInstance().getSortByDistance();
        return sortByDistance;
    }

    @Override
    public LiveData<Boolean> getShowMyLocation() {
        showMyLocation = SettingsFirebase.getInstance().getShowMyLocation();
        return showMyLocation;
    }

    @Override
    public void setDate(String date) {
        SettingsFirebase.getInstance().setDate(date);
    }

    @Override
    public void setSortByDistance(Boolean bool) {
        SettingsFirebase.getInstance().setSortByDistance(bool);
    }

    @Override
    public void setShowMyLocation(Boolean bool) {
        SettingsFirebase.getInstance().setShowMyLocation(bool);
    }
}
