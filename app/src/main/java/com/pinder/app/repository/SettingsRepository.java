package com.pinder.app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.pinder.app.persistance.SettingsFirebase;
import com.pinder.app.persistance.SettingsFirebaseDao;
import com.pinder.app.util.Resource;

public class SettingsRepository implements SettingsFirebaseDao {
    public static SettingsRepository instance = null;
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
    public void updateMyDb(Boolean dateValid, int logoutFlag) {
        SettingsFirebase.getInstance().updateMyDb(dateValid,logoutFlag);
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

    public void deleteWithRxJava(String userId){
        SettingsFirebase.getInstance().deleteWithRxJava(userId);
    }

    public void restartMatches(){
        SettingsFirebase.getInstance().restartMatches();
    }

    public MutableLiveData<Resource<Integer>> getLogoutLiveData() {
        return SettingsFirebase.getInstance().getLogoutLiveData();
    }
}
