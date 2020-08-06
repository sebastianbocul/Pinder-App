package com.pinder.app.settings;

import androidx.lifecycle.LiveData;

public interface SettingsFirebaseDao {
    void updateMyDb(Boolean dateValid);

    LiveData<String> getDate();

    LiveData<Boolean> getSortByDistance();

    LiveData<Boolean> getShowMyLocation();

    void setDate(String date);

    void setSortByDistance(Boolean bool);

    void setShowMyLocation(Boolean bool);
}
