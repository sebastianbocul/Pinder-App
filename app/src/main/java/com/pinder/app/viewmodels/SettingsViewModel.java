package com.pinder.app.viewmodels;

import android.app.Application;

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

public class SettingsViewModel extends AndroidViewModel implements SettingsFirebaseDao {
    private LiveData<String> date;
    private LiveData<Boolean> showMyLocation;
    private LiveData<Boolean> sortByDistance;
    private SettingsRepository settingsRepository = null;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        settingsRepository = SettingsRepository.getInstance();
    }

    @Override
    public void updateMyDb(Boolean dateValid) {
        settingsRepository.updateMyDb(dateValid);
    }

    @Override
    public LiveData<String> getDate() {
        date = settingsRepository.getDate();
        return date;
    }

    @Override
    public LiveData<Boolean> getSortByDistance() {
        sortByDistance = settingsRepository.getSortByDistance();
        return sortByDistance;
    }

    @Override
    public LiveData<Boolean> getShowMyLocation() {
        showMyLocation = settingsRepository.getShowMyLocation();
        return showMyLocation;
    }

    @Override
    public void setDate(String date) {
        settingsRepository.setDate(date);
    }

    @Override
    public void setSortByDistance(Boolean bool) {
        settingsRepository.setSortByDistance(bool);
    }

    @Override
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
