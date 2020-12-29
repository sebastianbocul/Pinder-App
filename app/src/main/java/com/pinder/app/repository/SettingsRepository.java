package com.pinder.app.repository;
import androidx.lifecycle.LiveData;
import com.pinder.app.persistance.SettingsFirebase;
import com.pinder.app.persistance.SettingsFirebaseDao;
import com.pinder.app.util.Resource;

public class SettingsRepository implements SettingsFirebaseDao {
    private SettingsFirebase settingsFirebase;
    private static final String TAG = "SettingsRepository";

    public SettingsRepository(SettingsFirebase settingsFirebase) {
        this.settingsFirebase = settingsFirebase;
    }

    @Override
    public void updateMyDb(Boolean dateValid, int logoutFlag) {
        settingsFirebase.updateMyDb(dateValid, logoutFlag);
    }

    @Override
    public LiveData<Resource<String>> getDate() {
        return settingsFirebase.getDate();
    }

    @Override
    public LiveData<Resource<Boolean>> getSortByDistance() {
        return settingsFirebase.getSortByDistance();
    }

    @Override
    public LiveData<Resource<Boolean>> getShowMyLocation() {
        return settingsFirebase.getShowMyLocation();
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
        return settingsFirebase.getLogoutLiveData();
    }
}
