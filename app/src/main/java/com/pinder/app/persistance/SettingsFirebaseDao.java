package com.pinder.app.persistance;

import androidx.lifecycle.LiveData;

public interface SettingsFirebaseDao {
    void updateMyDb(Boolean dateValid, int logoutFlag);

    LiveData<String> getDate();

    LiveData<Boolean> getSortByDistance();

    LiveData<Boolean> getShowMyLocation();

    void setDate(String date);

    void setSortByDistance(Boolean bool);

    void setShowMyLocation(Boolean bool);

    public void restartMatches();
}
