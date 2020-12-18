package com.pinder.app.di;


import android.app.Application;

import com.pinder.app.persistance.MainFirebase;
import com.pinder.app.repository.MainRepository;
import com.pinder.app.repository.ProfileRepository;
import com.pinder.app.viewmodels.MainViewModel;
import com.pinder.app.viewmodels.ProfileViewModel;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
class MainModules {
    @ActivityRetainedScoped
    @Provides
    public static MainFirebase mainFirebase(Application application) {
        return new MainFirebase(application);
    }

    @ActivityRetainedScoped
    @Provides
    public static MainRepository mainRepository(MainFirebase mainFirebase) {
        return new MainRepository(mainFirebase);
    }

    @ActivityRetainedScoped
    @Provides
    public static MainViewModel mainViewModel(MainRepository mainRepository) {
        return new MainViewModel(mainRepository);
    }
}