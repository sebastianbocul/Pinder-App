package com.pinder.app.persistance;

import androidx.lifecycle.LiveData;

import com.pinder.app.util.Resource;

public interface SettingsFirebaseDao {
    void updateMyDb(Boolean dateValid, int logoutFlag);

    LiveData<Resource<String>> getDate();

    void setDate(String date);

    LiveData<Resource<Boolean>> getSortByDistance();

    void setSortByDistance(Boolean bool);

    LiveData<Resource<Boolean>> getShowMyLocation();

    void setShowMyLocation(Boolean bool);

    void restartMatches();
}
