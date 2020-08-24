package com.pinder.app.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseUser;
import com.pinder.app.persistance.MainFirebase;
import com.pinder.app.persistance.MatchesFirebase;
import com.pinder.app.persistance.PopularTagsFirebase;
import com.pinder.app.persistance.ProfileFirebase;
import com.pinder.app.persistance.SettingsFirebase;
import com.pinder.app.persistance.SettingsFirebaseDao;
import com.pinder.app.persistance.TagsFirebase;
import com.pinder.app.repository.MainRepository;
import com.pinder.app.repository.MatchesRepository;
import com.pinder.app.repository.PopularTagsRepository;
import com.pinder.app.repository.ProfileRepository;
import com.pinder.app.repository.SettingsRepository;
import com.pinder.app.repository.TagsRepository;

public class SettingsViewModel extends AndroidViewModel {
    private LiveData<String> date;
    private LiveData<Boolean> showMyLocation;
    private LiveData<Boolean> sortByDistance;
    private SettingsRepository settingsRepository = null;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        settingsRepository = SettingsRepository.getInstance();
    }

    public void updateMyDb(Boolean dateValid) {
        settingsRepository.updateMyDb(dateValid);
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

    public void deleteWithRxJava(String userId){
        settingsRepository.deleteWithRxJava(userId);
    }


    public void restartMatches(){
        settingsRepository.restartMatches();
    }
}
