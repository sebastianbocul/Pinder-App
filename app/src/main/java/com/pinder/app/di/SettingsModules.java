package com.pinder.app.di;

import android.content.Context;

import com.pinder.app.cache.SettingsCache;
import com.pinder.app.persistance.SettingsFirebase;
import com.pinder.app.repository.SettingsRepository;
import com.pinder.app.viewmodels.SettingsViewModel;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
class SettingsModules {
    @ActivityRetainedScoped
    @Provides
    public static SettingsCache settingsCache(@ApplicationContext Context context) {
        return new SettingsCache(context);
    }

    @ActivityRetainedScoped
    @Provides
    public static SettingsFirebase settingsFirebase() {
        return new SettingsFirebase();
    }

    @ActivityRetainedScoped
    @Provides
    public static SettingsRepository settingsRepository(SettingsFirebase settingsFirebase,SettingsCache settingsCache) {
        return new SettingsRepository(settingsFirebase,settingsCache);
    }

    @ActivityRetainedScoped
    @Provides
    public static SettingsViewModel settingsViewModel(SettingsRepository settingsRepository) {
        return new SettingsViewModel(settingsRepository);
    }
}