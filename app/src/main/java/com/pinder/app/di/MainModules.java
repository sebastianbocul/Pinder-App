package com.pinder.app.di;


import android.app.Application;
import android.content.Context;

import com.pinder.app.cache.MainCache;
import com.pinder.app.persistance.MainFirebase;
import com.pinder.app.repository.MainRepository;
import com.pinder.app.repository.ProfileRepository;
import com.pinder.app.viewmodels.MainViewModel;
import com.pinder.app.viewmodels.ProfileViewModel;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
class MainModules {
    @ActivityRetainedScoped
    @Provides
    public static MainFirebase mainFirebase(@ApplicationContext Context context) {
        return new MainFirebase(context);
    }
    @ActivityRetainedScoped
    @Provides
    public static MainCache mainCache(@ApplicationContext Context context) {
        return new MainCache(context);
    }

    @ActivityRetainedScoped
    @Provides
    public static MainRepository mainRepository(MainFirebase mainFirebase,MainCache mainCache) {
        return new MainRepository(mainFirebase,mainCache);
    }

    @ActivityRetainedScoped
    @Provides
    public static MainViewModel mainViewModel(MainRepository mainRepository) {
        return new MainViewModel(mainRepository);
    }
}