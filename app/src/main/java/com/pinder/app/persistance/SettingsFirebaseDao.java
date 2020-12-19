package com.pinder.app.persistance;

import androidx.lifecycle.LiveData;

import com.pinder.app.util.Resource;

public interface SettingsFirebaseDao {
    void updateMyDb(Boolean dateValid, int logoutFlag);

    LiveData<Resource<String>> getDate();

    LiveData<Resource<Boolean>> getSortByDistance();

    LiveData<Resource<Boolean>> getShowMyLocation();

    void setDate(String date);

    void setSortByDistance(Boolean bool);

    void setShowMyLocation(Boolean bool);

    void restartMatches();
}
